
// @GENERATOR:play-routes-compiler
// @SOURCE:/home/raghav/nowfloats-channel-api/conf/routes
// @DATE:Mon Oct 16 14:35:50 IST 2017

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseHomeController HomeController = new controllers.ReverseHomeController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseUserChannelController UserChannelController = new controllers.ReverseUserChannelController(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseHomeController HomeController = new controllers.javascript.ReverseHomeController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseUserChannelController UserChannelController = new controllers.javascript.ReverseUserChannelController(RoutesPrefix.byNamePrefix());
  }

}
