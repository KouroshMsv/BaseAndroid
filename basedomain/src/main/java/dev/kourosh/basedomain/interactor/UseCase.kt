package dev.kourosh.basedomain.interactor

interface UseCase<R :  Any?> {

  fun execute(): R
}
