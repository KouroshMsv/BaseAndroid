package dev.kourosh.basedomain.interactor

interface SuspendUseCaseWithParameter<in P : Any, R : Any?> {
    suspend fun execute(parameter: P): R
}
