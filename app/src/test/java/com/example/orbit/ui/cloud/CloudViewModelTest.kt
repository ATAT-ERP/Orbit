package com.example.orbit.ui.cloud

import com.example.orbit.dto.ActiveCompany
import com.example.orbit.dto.CloudFileDto
import com.example.orbit.dto.CloudRepository
import com.example.orbit.dto.CloudService
import com.example.orbit.dto.CompanyDto
import com.example.orbit.dto.DownloadUrlDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CloudViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private val empresa = CompanyDto(
        id = "9964b55a-ef5a-4745-b4a5-9c4e053550a8",
        name = "Empresa de prueba"
    )

    private val otraEmpresa = CompanyDto(
        id = "3f2a18c6-5d71-4c02-9b44-7e1d6a0c8b35",
        name = "Segunda"
    )

    private val archivo = CloudFileDto(
        id = "1034c0bb-3854-40b3-a47a-eedba97f6a83",
        name = "Nietzsche - temas generales",
        originalName = "Nietzsche.pdf",
        mimeType = "application/pdf",
        size = 3081,
        createdAt = "2026-10-29T04:51:33.645669Z"
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        ActiveCompany.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun `con una empresa elegida lista sus archivos`() {
        val service = FakeCloudService(mapOf(empresa.id to listOf(archivo)))
        ActiveCompany.select(empresa)

        val viewModel = CloudViewModel(CloudRepository(service))

        val state = viewModel.uiState.value as CloudUiState.Content
        assertEquals(listOf(archivo), state.files)
        assertEquals(listOf(empresa.id), service.consultas)
    }

    @Test
    fun `una empresa sin archivos queda en el estado vacio`() {
        ActiveCompany.select(empresa)

        val viewModel = CloudViewModel(CloudRepository(FakeCloudService()))

        assertTrue(viewModel.uiState.value is CloudUiState.Empty)
    }

    @Test
    fun `un acceso no autorizado se explica en lugar de hablar de documentos`() {
        val cuerpo = """{"code":"NEX-DOC-002","message":"Documento no encontrado."}"""
            .toResponseBody("application/json".toMediaType())
        val service = FakeCloudService(respuestaListado = { Response.error(404, cuerpo) })
        ActiveCompany.select(empresa)

        val viewModel = CloudViewModel(CloudRepository(service))

        val state = viewModel.uiState.value as CloudUiState.Error
        assertEquals("No tenés acceso a los archivos de esta empresa.", state.message)
    }

    @Test
    fun `al cambiar de empresa pide los archivos de la nueva`() {
        val service = FakeCloudService(mapOf(empresa.id to listOf(archivo)))
        ActiveCompany.select(empresa)
        val viewModel = CloudViewModel(CloudRepository(service))

        ActiveCompany.select(otraEmpresa)

        assertEquals(listOf(empresa.id, otraEmpresa.id), service.consultas)
        assertTrue(viewModel.uiState.value is CloudUiState.Empty)
    }

    @Test
    fun `descargar entrega la url firmada con el nombre original`() = runTest(dispatcher) {
        val service = FakeCloudService(mapOf(empresa.id to listOf(archivo)))
        ActiveCompany.select(empresa)
        val viewModel = CloudViewModel(CloudRepository(service))

        val eventos = mutableListOf<CloudDownload>()
        val suscripcion = launch { viewModel.download.toList(eventos) }

        viewModel.downloadFile(archivo)

        assertEquals(
            CloudDownload.Ready("https://storage.example/firmada", "Nietzsche.pdf"),
            eventos.single()
        )
        assertEquals(listOf(archivo.id), service.descargas)
        suscripcion.cancel()
    }

    @Test
    fun `si falla la red al descargar se informa el problema`() = runTest(dispatcher) {
        val service = FakeCloudService(
            mapOf(empresa.id to listOf(archivo)),
            respuestaDescarga = { throw IOException("sin red") }
        )
        ActiveCompany.select(empresa)
        val viewModel = CloudViewModel(CloudRepository(service))

        val eventos = mutableListOf<CloudDownload>()
        val suscripcion = launch { viewModel.download.toList(eventos) }

        viewModel.downloadFile(archivo)

        assertEquals(
            CloudDownload.Failed("No se pudo conectar. Revisá tu conexión e intentá de nuevo."),
            eventos.single()
        )
        suscripcion.cancel()
    }

    private class FakeCloudService(
        private val archivos: Map<String, List<CloudFileDto>> = emptyMap(),
        private val respuestaListado: (String) -> Response<List<CloudFileDto>> = { companyId ->
            Response.success(archivos[companyId].orEmpty())
        },
        private val respuestaDescarga: suspend () -> Response<DownloadUrlDto> = {
            Response.success(DownloadUrlDto("https://storage.example/firmada"))
        }
    ) : CloudService {
        val consultas = mutableListOf<String>()
        val descargas = mutableListOf<String>()

        override suspend fun getFiles(companyId: String): Response<List<CloudFileDto>> {
            consultas += companyId
            return respuestaListado(companyId)
        }

        override suspend fun getDownloadUrl(fileId: String): Response<DownloadUrlDto> {
            descargas += fileId
            return respuestaDescarga()
        }
    }
}