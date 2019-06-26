package dev.kourosh.basedomain.interactor

interface UseCaseWithParameter<in P : Any, R : Any?> {
  fun execute(parameter: P): R
}
