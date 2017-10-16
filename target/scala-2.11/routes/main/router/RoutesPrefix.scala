
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/raghav/nowfloats-channel-api/conf/routes
// @DATE:Mon Oct 16 14:35:50 IST 2017


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
