package me.dio.credit.exception

data class BusinessException(override val message: String?) : RuntimeException() {

}
