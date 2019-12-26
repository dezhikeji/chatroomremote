package common

/**
 * Created by 林 on 14-3-27.
 */
class VenusException(msg: String) extends Exception(msg) {
  //更多的详情
  val detail: String = ""
}

class UnSupportQueryExcepiton(val ref: Any) extends VenusException("请求对象未定义")

class UnSupportExcepiton extends VenusException("不支持的操作，赶紧反馈给程序猿")

class EmptyFieldExcepiton extends VenusException("缺少必要数据")

class DataOverExcepiton extends VenusException("当前访问数据不是你的")

class DataNoFindExcepiton extends VenusException("访问的数据不存在")

class NoPassException extends VenusException("验证失败")

class RequstLimitExcepiton extends VenusException("操作频率限制，请稍候再试")

class NeedLoginExcepiton extends VenusException("需要登录才能访问")

class ForbidenExcepiton extends VenusException("你已经被禁止登录，请联系管理员解封")