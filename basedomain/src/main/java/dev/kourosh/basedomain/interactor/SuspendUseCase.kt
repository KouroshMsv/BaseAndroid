package dev.kourosh.basedomain.interactor


interface SuspendUseCase<R : Any?> {

  suspend fun execute(): R
}
