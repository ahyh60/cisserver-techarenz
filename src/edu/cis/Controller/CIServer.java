/*
 * File: CIServer.java
 * ------------------------------
 * When it is finished, this program will implement a basic
 * ecommerce network management server.  Remember to update this comment!
 */

package edu.cis.Controller;

import acm.program.*;
import edu.cis.Model.*;
import edu.cis.Utils.SimpleServer;

import java.util.ArrayList;
import java.util.Objects;

public class CIServer extends ConsoleProgram
        implements SimpleServerListener
{

    /* The internet port to listen to requests on */
    private static final int PORT = 8000;

    /* The server object. All you need to do is start it */
    private SimpleServer server = new SimpleServer(this, PORT);
    private ArrayList<CISUser> users =  new ArrayList<CISUser>();; // A list containing all the CISUser that have been created
    private ArrayList<MenuItem> menuList =  new ArrayList<MenuItem>();
    private Menu menu = new Menu(menuList); // A Menu object that will hold all MenuItems


    /**
     * Starts the server running so that when a program sends
     * a request to this server, the method requestMade is
     * called.
     */
    public void run()
    {
        println("Starting server on port " + PORT);
        server.start();
    }

    /**
     * When a request is sent to this server, this method is
     * called. It must return a String.
     *
     * @param request a Request object built by SimpleServer from an
     *                incoming network request by the client
     */
    public String requestMade(Request request)
    {
        String cmd = request.getCommand();
        println(request.toString());
        println(request.getCommand());
        // your code here.
        if (request.getCommand().equals(CISConstants.PING))
        {
            final String PING_MSG = "Hello, internet";
            
            println("   => " + PING_MSG);
            return PING_MSG;
        }
        if(request.getCommand().equals(CISConstants.CREATE_USER))
        {
            return createUser(request);
        }
        else if (request.getCommand().equals(CISConstants.ADD_MENU_ITEM))
        {
            return addMenuItem(request);
        }
        else if (request.getCommand().equals(CISConstants.PLACE_ORDER))
        {
            return placeOrder(request);
        }
        else if (request.getCommand().equals(CISConstants.DELETE_ORDER))
        {
            return deleteOrder(request);
        }
        else if (request.getCommand().equals(CISConstants.GET_ORDER))
        {
            return getOrder(request);
        }
        else if (request.getCommand().equals(CISConstants.GET_ITEM))
        {
            return getItem(request);
        }
        else if (request.getCommand().equals(CISConstants.GET_USER))
        {
            return getUser(request);
        }
        else if (request.getCommand().equals(CISConstants.GET_CART))
        {
            return getCart(request);
        }

        return "Error: Unknown command " + cmd + ".";
    }

    public String createUser(Request req)
    {
        String name = req.getParam(CISConstants.USER_NAME_PARAM);
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        String yearLevel = req.getParam(CISConstants.YEAR_LEVEL_PARAM);

        for (CISUser u: users){
            if (u.getUserID() == userId){
                return CISConstants.DUP_USER_ERR;
            }
        }
        if (name == null || userId == null || yearLevel == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        CISUser newUser =  new CISUser(userId, name, yearLevel,50.0);
        users.add(newUser);
        return CISConstants.SUCCESS;
    }

    public String addMenuItem(Request req) {
        String itemName = req.getParam(CISConstants.ITEM_NAME_PARAM);
        String description = req.getParam(CISConstants.DESC_PARAM);
        double price = Double.parseDouble(req.getParam(CISConstants.PRICE_PARAM));
        String itemType = req.getParam(CISConstants.ITEM_TYPE_PARAM);
        String itemId = req.getParam(CISConstants.ITEM_ID_PARAM);

        MenuItem newItem = new MenuItem(itemId, itemName, description, price, itemType);
        menu.addEadriumItem(newItem);

        return "success";
    }

    public String placeOrder(Request req) {
        String orderId = req.getParam(CISConstants.ORDER_ID_PARAM);
        String menuItemId = req.getParam(CISConstants.ITEM_ID_PARAM);
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        String orderType = req.getParam(CISConstants.ORDER_TYPE_PARAM);

        if(orderId == null) {
            return CISConstants.ORDER_INVALID_ERR;
        }
        if ( menuList.size() == 0){
            return CISConstants.EMPTY_MENU_ERR;
        }

        // user exist?
        CISUser user = new CISUser();
        boolean userExist = false;
        for (CISUser u:users){
            if (Objects.equals(u.getUserID(), userId)){
                user = u;
                userExist = true;
                break;
            }
        }
        if (!userExist){
            return CISConstants.USER_INVALID_ERR;
        }

        boolean exists = false;
        for (Order o: user.getOrders()){
            if (Objects.equals(o.getOrderID(), orderId)){
                exists = true;
                break;
            }
        }
        if (exists){
            return CISConstants.DUP_ORDER_ERR;
        }

        for (CISUser u: users) {
            if(Objects.equals(u.getUserID(), userId)) continue;
            for (Order o: u.getOrders()){
                if (Objects.equals(o.getOrderID(), orderId)) {
                    return CISConstants.ORDER_INVALID_ERR;
                }
            }
        }

        MenuItem item = new MenuItem();
        boolean itemExist = false;
        for (MenuItem m: menu.getEadriumItems()){
            if (Objects.equals(m.getId(), menuItemId)){
                item = m;
                itemExist = true;
                break;
            }
            if (Objects.equals(m.getId(), menuItemId) && m.getAmountAvailable() < 1){
                return CISConstants.SOLD_OUT_ERR;
            }
        }
        if (!itemExist){
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }
        if (item.getPrice()>user.getMoney()){
            return CISConstants.USER_BROKE_ERR;
        }
        Order order = new Order(menuItemId, orderType, orderId);
        item.minusAmountAvailable();
        user.getOrders().add(order);
        user.spend(item.getPrice());

        return CISConstants.SUCCESS;
    }


    public String deleteOrder(Request req) {
        String orderId = req.getParam(CISConstants.ORDER_ID_PARAM);
        String userId = req.getParam(CISConstants.USER_ID_PARAM);

        //user exist?
        CISUser user = new CISUser();
        boolean userExist = false;
        for (CISUser u:users){
            if (Objects.equals(u.getUserID(), userId)){
                user = u;
                userExist = true;
                break;
            }
        }
        if (!userExist){
            return CISConstants.USER_INVALID_ERR;
        }
        boolean exists = false;
        for (Order o: user.getOrders()){
            if (Objects.equals(o.getOrderID(), orderId)){
                exists = true;
                user.getOrders().remove(o);
                break;
            }
        }
        if (!exists){
            return CISConstants.ORDER_INVALID_ERR;
        }
        return CISConstants.SUCCESS;
    }

    public String getOrder(Request req){
        String orderId = req.getParam(CISConstants.ORDER_ID_PARAM);
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        CISUser user = new CISUser();
        boolean exists = false;
        for (CISUser u:users){
            if (Objects.equals(u.getUserID(), userId)){
                user = u;
                exists = true;
                break;
            }
        }
        if (!exists){
            return "Error: user does not exist";
        }
        Order order = new Order();
        boolean orderExist = false;
        for (Order o: user.getOrders()){
            if (Objects.equals(o.getOrderID(), orderId)){
                order = o;
                orderExist = true;
                break;
            }
        }
        if (!orderExist){
            return "Error: order don't exist";
        }
        return order.toString();
    }

    public String getUser(Request req){
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        CISUser user = new CISUser();
        boolean userExist = false;
        for (CISUser u:users){
            if (u.getUserID().equals(userId) ){
                user = u;
                userExist = true;
                break;
            }
        }
        if (!userExist){
            return CISConstants.USER_INVALID_ERR;
        }
        return user.toString();
    }

    public String getItem(Request req){
        String itemId = req.getParam(CISConstants.ITEM_ID_PARAM);
        MenuItem item = new MenuItem();
        boolean itemExist = false;
        for (MenuItem m: menu.getEadriumItems()){
            if (m.getId().equals(itemId)){
                item = m;
                itemExist = true;
                break;
            }
        }
        if (!itemExist){
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }

        String message = item.toString();
        print(message);
        return message;
    }

    public String getCart(Request req){
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        CISUser user = new CISUser();
        boolean userExist = false;
        for (CISUser u:users){
            if (Objects.equals(u.getUserID(), userId)){
                user = u;
                userExist = true;
                break;
            }
        }
        if (!userExist){
            return CISConstants.USER_INVALID_ERR;
        }

        return "orders="+user.orderToString();
    }



    public static void main(String[] args)
    {
        CIServer f = new CIServer();
        f.start(args);
    }
}
