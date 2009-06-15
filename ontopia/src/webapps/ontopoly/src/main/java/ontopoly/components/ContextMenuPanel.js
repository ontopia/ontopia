function browserTest(){
    var agt=navigator.userAgent.toLowerCase();

    is_DOM = (document.getElementById) ? true : false;
    var is_opera = (agt.indexOf("opera") != -1) ? true:false;
    var is_konq = agt.indexOf('konqueror') != -1 ? true:false;
    var is_safari = ((agt.indexOf('safari')!=-1)&&(agt.indexOf('mac')!=-1))?true:false;
    var is_khtml  = (is_safari || is_konq);
    var is_gecko = ((!is_khtml)&&(navigator.product)&&(navigator.product.toLowerCase()=="gecko"))?true:false;
    var is_moz   = ((agt.indexOf('mozilla/5')!=-1) && (agt.indexOf('spoofer')==-1) &&
                    (agt.indexOf('compatible')==-1) && (agt.indexOf('opera')==-1)  &&
                    (agt.indexOf('webtv')==-1) && (agt.indexOf('hotjava')==-1)     &&
                    (is_gecko) && 
                    ((navigator.vendor=="")||(navigator.vendor=="Mozilla")));
}

/* get mouse position */
var Mouse = {x:0, y:0};

document.onmousemove = function (evt) {

    var e = window.event || evt;
    Mouse.x = e.pageX | e.clientX  || 0;
    Mouse.y = e.pageY || e.clientY + document.body.scrollTop || 0;
}
/* get mouse position end */

browser_DOM = (document.getElementById) ? true : false;
browser_Khtml = (navigator.product) ? (navigator.product.indexOf('khtml') >= 0) : false;
browser_IE = (document.all) ? (!browser_Khtml) : false;
browser_NS = (document.layers) ? true : ((navigator.vendor) && (navigator.vendor.indexOf('Netscape') >=0 ));
browser_Mac = (navigator.appVersion.indexOf("Mac") != -1);
browser_Gecko = (browser_DOM && browser_NS) || ((navigator.product) && (navigator.product.indexOf('Gecko') >= 0));

/*
 * send onShow event to a menuitem's submenu
 */

function menu_show(menu) {
    
    menu.onShow();
}

/*
 * Send focus event to a menuItem
 */
function item_focus(menuItem) {
  if ((!menuItem.subMenu) && (!menuItem.noSubMenu)) {
    var subID;
    if ( menuItem.getAttribute("id") && 
         menuItem.getAttribute("id").match(/main(.+)/)) {
      subID = "m" + RegExp.$1;
    }

    menuItem.subMenu = document.getElementById(subID);
    menuItem.noSubMenu = (menuItem.subMenu == null);
    if (menuItem.subMenu) {
      menuItem.subMenu.parent = menuItem;
      menuInit(menuItem.subMenu);
      menuItem.subMenu.setPosition();
    }
  } else {
    //We position the menu each time to make sure it doesn't move on window resize
    menuItem.subMenu.setPosition();
  }

  if (menuItem.subMenu) {
    menu_show(menuItem.subMenu);
    menuMouseoutM(menuItem.subMenu, 4000);
  }
}

/*
 * Send onNoFocus event to a menu
 */

function menu_unFocus(menu) {

    /* Hide this menu */
	if (menu.onHide != null)
      menu.onHide();
    return true;
}

function menuItemPopup(menuItem) {
    menuItem.menu = menuItem;
    item_focus(menuItem);
}

function menuMouseout(menu) {

    menuMouseoutM(menu.menu, 4000);
}

function menuMouseoutM(menu, timeout) {

      menu.hideEvent = setTimeout("menu_unFocus(document.getElementById('" + menu.id + "'));", timeout);
}

function menuMouseover(menu) {
    clearTimeout(menu.menu.hideEvent);
}

function menuInit(subMenu) {

    var i, itemList;
    itemList = subMenu.getElementsByTagName("a");
    for (i = 0; i < itemList.length; i++) {
      itemList[i].menu = subMenu;
      itemList[i].onmouseout = new Function("menuMouseout(this);");
      itemList[i].onmouseover = new Function("menuMouseover(this);");
    }

    subMenu.setPosition = function() {

    var mWidth = subMenu.offsetWidth;
    var x, y, z;
    mParent = new PageOffset(subMenu.parent);

    y = mParent.y + (subMenu.parent.offsetHeight / 2);
    x = mParent.x + (subMenu.parent.offsetWidth / 2);

    subMenu.style.left = x + "px";
    subMenu.style.top  = y + "px";
    
    if(subMenu.offsetWidth < 1 || !browser_IE){
      subMenu.style.width = mWidth;
    }

    if(subMenu.offsetWidth < subMenu.parent.offsetWidth){
      subMenu.style.width = subMenu.parent.offsetWidth + "px";
    }    
  }
    
    subMenu.onHide = function() {
    subMenu.style.visibility = "hidden";
    }

    // return true if menu is displayed
    subMenu.onShow = function() {
    subMenu.style.visibility = "visible";
    }

    return subMenu;
}

function PageOffset(el) {
    this.x = el.offsetLeft;
    this.y = el.offsetTop;
    if (el.offsetParent != null) {
var z;
z = new PageOffset(el.offsetParent);
this.x += z.x;
this.y += z.y;
    }
    
    return;
}