package com.example.orbit.ui.profile

import com.example.orbit.dto.UserDto
import com.example.orbit.dto.UserRepository
import com.example.orbit.dto.UserService
import com.example.orbit.dto.UserUpdateDto
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val user = UserDto(
        id = "320b70b4-9d48-4a45-beee-31a3ca23223e",
        email = "uzi1234@prueba.com",
        firstName = "Ramiro",
        lastName = "Ramirez",
        avatarPath = null,
        isActive = true,
        isSystemAdmin = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `al iniciar carga el perfil desde el backend`() {
        val viewModel = ProfileViewModel(UserRepository(FakeUserService(user)))

        val state = viewModel.uiState.value as ProfileUiState.Content
        assertEquals("Ramiro", state.firstName)
        assertEquals("Ramirez", state.lastName)
    }

    @Test
    fun `guardar con nombre vacio no llama al backend y marca el error`() {
        val service = FakeUserService(user)
        val viewModel = ProfileViewModel(UserRepository(service))

        viewModel.onFirstNameChange("   ")
        viewModel.saveProfile()

        val state = viewModel.uiState.value as ProfileUiState.Content
        assertEquals("Este campo es obligatorio.", state.firstNameError)
        assertTrue(service.updates.isEmpty())
    }

    @Test
    fun `guardar envia nombre y apellido sin espacios y muestra lo devuelto`() {
        val service = FakeUserService(user)
        val viewModel = ProfileViewModel(UserRepository(service))

        viewModel.onFirstNameChange("  Uziel ")
        viewModel.onLastNameChange(" Ramos  ")
        viewModel.saveProfile()

        assertEquals(listOf(UserUpdateDto("Uziel", "Ramos")), service.updates)
        val state = viewModel.uiState.value as ProfileUiState.Content
        assertEquals("Uziel", state.user.firstName)
        assertEquals("Ramos", state.user.lastName)
        assertFalse(state.hasChanges)
    }

    @Test
    fun `error del backend conserva lo escrito y muestra el mensaje`() {
        val errorBody = """{"code":"NEX-USR-003","message":"Los datos enviados no son válidos."}"""
            .toResponseBody("application/json".toMediaType())
        val service = FakeUserService(user) { Response.error(400, errorBody) }
        val viewModel = ProfileViewModel(UserRepository(service))

        viewModel.onFirstNameChange("Uziel")
        viewModel.saveProfile()

        val state = viewModel.uiState.value as ProfileUiState.Content
        assertEquals("Uziel", state.firstName)
        assertFalse(state.isSaving)
        assertEquals("Los datos enviados no son válidos.", state.saveError)
    }

    @Test
    fun `mientras guarda no envia una segunda solicitud`() {
        val pendingResponse = CompletableDeferred<Response<UserDto>>()
        val service = FakeUserService(user) { pendingResponse.await() }
        val viewModel = ProfileViewModel(UserRepository(service))

        viewModel.onFirstNameChange("Uziel")
        viewModel.saveProfile()
        viewModel.saveProfile()

        assertEquals(1, service.updates.size)
        assertTrue((viewModel.uiState.value as ProfileUiState.Content).isSaving)
    }

    private class FakeUserService(
        private val user: UserDto,
        private val respondToUpdate: suspend (UserUpdateDto) -> Response<UserDto> = { update ->
            Response.success(user.copy(firstName = update.firstName, lastName = update.lastName))
        }
    ) : UserService {
        val updates = mutableListOf<UserUpdateDto>()

        override suspend fun getCurrentUser(): Response<UserDto> = Response.success(user)

        override suspend fun updateCurrentUser(update: UserUpdateDto): Response<UserDto> {
            updates += update
            return respondToUpdate(update)
        }
    }
}