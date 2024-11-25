package hu.bme.aut.citysee.domain.usecases

class IsEmailValidUseCase {

    fun validate(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.EmptyEmail
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> ValidationResult.InvalidEmail
            else -> ValidationResult.Valid
        }
    }

    sealed class ValidationResult {
        object Valid : ValidationResult()
        object EmptyEmail : ValidationResult()
        object InvalidEmail : ValidationResult()
    }
}
