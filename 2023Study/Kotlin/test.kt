fun main(args: Array<String>) {
    throw CustomException()
}
// 自定义异常
class CustomException : IllegalArgumentException("illegal")