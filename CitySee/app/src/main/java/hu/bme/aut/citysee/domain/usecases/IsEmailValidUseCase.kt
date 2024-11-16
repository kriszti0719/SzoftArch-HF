package hu.bme.aut.citysee.domain.usecases

class IsEmailValidUseCase {

    operator fun invoke(email: String): Boolean =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

}