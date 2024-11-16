package hu.bme.aut.citysee.domain.usecases

class PasswordsMatchUseCase {

    operator fun invoke(password: String, confirmPassword: String): Boolean =
        password == confirmPassword
}