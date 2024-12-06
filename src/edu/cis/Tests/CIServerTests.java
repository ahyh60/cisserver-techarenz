package edu.cis.Tests;

import acm.program.*;
import edu.cis.Model.CISConstants;
import edu.cis.Model.Request;
import edu.cis.Utils.SimpleClient;

import java.io.*;


public class CIServerTests extends ConsoleProgram
{

    public static void main(String[] args)
    {
        CIServerTests f = new CIServerTests();
        f.start(args);
    }

    /* The internet address of the computer running the server */
    private static final String HOST = "http://localhost:8000/";

    /* Run all of the tests */
    public void run()
    {

        // a welcome message
        println("Running the CIServer tester");
        println("Make sure that the server has just been restarted");
        println("-------");
        println("");

        int passed = 0;
        int total = 0;

        println("=== PING ===\n");

        Request ping = new Request("ping");
        boolean success = runTest(ping, false, "Hello, internet");
        total++;
        if (success)
        {
            passed++;
        }

        println("=== ADD Create Users ===\n");

        String[] userIDs = {"abcd", "90abnm", "plmnk8", "qwert5", "876yhb"};
        String[] userNames = {"sierra", "keysha", "jazmine", "latisha", "kiera"};
        String[] yearLevel = {"y12", "y12", "y13", "y13", "y11"};

        //creating users should be successful
        for (int i = 0; i < userIDs.length; i++)
        {
            Request addRequest = new Request(CISConstants.CREATE_USER);

            addRequest.addParam(CISConstants.USER_ID_PARAM, userIDs[i]);
            addRequest.addParam(CISConstants.USER_NAME_PARAM, userNames[i]);
            addRequest.addParam(CISConstants.YEAR_LEVEL_PARAM, yearLevel[i]);

            success = runTest(addRequest, false, CISConstants.SUCCESS);
            total++;
            if (success)
            {
                passed++;
            }
        }

        String[] testItemNames = {"Steak", "Cheerios", "Dumplings", "Burger", "Laksa"};
        String[] testdDesc = {"Ribeye steak with veggies and chimichurri",
                "With almond milk.",
                "Pork and shrimp filling with vinegar and soy sauce.",
                "Cheesey and delicious. Beyond meat option available.",
                "East Coast Laksa Style"};
        int[] prices = {19, 20, 21, 22, 23};
        String[] itemIDs = {"6a6a", "777", "89b89b", "33a", "90pt"};
        int[] avail = {8, 7, 6, 5, 4};

        String[] orderIDs = {"okn90", "876ygv", "ygfc43", "9078ppp", "675liuy"};
        String[] orderType = {"lunch", "drink", "snack", "breakfast", "lunch"};

        println("=== PLACE Invalid Orders ===\n");

        // Placing orders a first time should not be successful because the menu is empty
        for (int i = 0; i < testItemNames.length; i++)
        {
            Request addRequest = new Request(CISConstants.PLACE_ORDER);

            addRequest.addParam(CISConstants.ORDER_ID_PARAM, orderIDs[i]);
            addRequest.addParam(CISConstants.USER_ID_PARAM, userIDs[i]);
            addRequest.addParam(CISConstants.ORDER_TYPE_PARAM, orderType[i]);
            addRequest.addParam(CISConstants.ITEM_ID_PARAM, itemIDs[i]);

            success = runTest(addRequest, true, "success");
            total++;
            if (success)
            {
                passed++;
            }
        }

        println("=== Add Items to Today's Menu ===\n");

        // Add Items to the Menu first
        for (int i = 0; i < testItemNames.length; i++)
        {
            Request addRequest = new Request(CISConstants.ADD_MENU_ITEM);

            addRequest.addParam(CISConstants.ITEM_NAME_PARAM, testItemNames[i]);
            addRequest.addParam(CISConstants.DESC_PARAM, testdDesc[i]);
            addRequest.addParam(CISConstants.PRICE_PARAM, String.valueOf(prices[i]));
            addRequest.addParam(CISConstants.ITEM_ID_PARAM, itemIDs[i]);
            addRequest.addParam(CISConstants.ITEM_TYPE_PARAM, orderType[i]);

            success = runTest(addRequest, false, "success");
            total++;
            if (success)
            {
                passed++;
            }
        }

        println("=== These orders should now pass ===\n");

        // Placing orders a first time should not be successful because the menu is empty
        for (int i = 0; i < testItemNames.length; i++)
        {
            Request addRequest = new Request(CISConstants.PLACE_ORDER);

            addRequest.addParam(CISConstants.ORDER_ID_PARAM, orderIDs[i]);
            addRequest.addParam(CISConstants.USER_ID_PARAM, userIDs[i]);
            addRequest.addParam(CISConstants.ORDER_TYPE_PARAM, orderType[i]);
            addRequest.addParam(CISConstants.ITEM_ID_PARAM, itemIDs[i]);

            success = runTest(addRequest, false, "success");
            total++;
            if (success)
            {
                passed++;
            }
        }



        println("=== DELETE and RE-ORDER ===\n");

        //first we try to place an order that exists
        Request d3 = new Request(CISConstants.PLACE_ORDER);
        d3.addParam(CISConstants.USER_ID_PARAM, "abcd"); //sierra
        d3.addParam(CISConstants.ORDER_ID_PARAM, "okn90"); //steak lunch
        d3.addParam(CISConstants.ITEM_ID_PARAM, "6a6a"); //steak lunch
        d3.addParam(CISConstants.ORDER_TYPE_PARAM, "lunch"); //steak lunch
        success = runTest(d3, true, "");
        total++;
        if (success)
        {
            passed++;
        }

        // then we delete that existing order
        Request d1 = new Request(CISConstants.DELETE_ORDER);
        d1.addParam(CISConstants.USER_ID_PARAM, "abcd"); //sierra
        d1.addParam(CISConstants.ORDER_ID_PARAM, "okn90"); //steak lunch
        success = runTest(d1, false, "success");
        total++;
        if (success)
        {
            passed++;
        }

        //should not return an error if sierra orders a steak lunch again
        Request d2 = new Request(CISConstants.PLACE_ORDER);
        d2.addParam(CISConstants.USER_ID_PARAM, "abcd"); //sierra
        d2.addParam(CISConstants.ORDER_ID_PARAM, "okn90"); //steak lunch
        d2.addParam(CISConstants.ITEM_ID_PARAM, "6a6a"); //steak lunch
        d2.addParam(CISConstants.ORDER_TYPE_PARAM, "lunch"); //steak lunch
        success = runTest(d2, false, "success");
        total++;
        if (success)
        {
            passed++;
        }


        // try to delete an order that doesnt exist
        Request b1 = new Request(CISConstants.DELETE_ORDER);
        b1.addParam(CISConstants.USER_ID_PARAM, "abcd"); //sierra
        b1.addParam(CISConstants.ORDER_ID_PARAM, "iiiii88"); //steak lunch
        success = runTest(b1, true, "success");
        total++;
        if (success)
        {
            passed++;
        }


        println("=== CHECK OVERSPEND ===\n");

        //this should put sierra over her spending money of 50
        Request a2 = new Request(CISConstants.PLACE_ORDER);
        a2.addParam(CISConstants.USER_ID_PARAM, "abcd"); //sierra
        a2.addParam(CISConstants.ORDER_ID_PARAM, "pop90"); //new order lunch
        a2.addParam(CISConstants.ITEM_ID_PARAM, "6a6a"); //steak lunch
        a2.addParam(CISConstants.ORDER_TYPE_PARAM, "lunch"); //steak lunch
        success = runTest(a2, true, "error");
        total++;
        if (success)
        {
            passed++;
        }

        println("=== INVALID ORDERS ===\n");
        //user doesnt exist
        Request d33 = new Request(CISConstants.PLACE_ORDER);
        d33.addParam(CISConstants.USER_ID_PARAM, "nmonb"); //sierra
        d33.addParam(CISConstants.ORDER_ID_PARAM, "mpkn"); //steak lunch
        d33.addParam(CISConstants.ITEM_ID_PARAM, "6a6a"); //steak lunch
        d33.addParam(CISConstants.ORDER_TYPE_PARAM, "lunch"); //steak lunch
        success = runTest(d33, true, "error");
        total++;
        if (success)
        {
            passed++;
        }

        //sierra has order with ID okn90, this order uses that same id for a different person
        Request dup = new Request(CISConstants.PLACE_ORDER);
        dup.addParam(CISConstants.USER_ID_PARAM, "90abnm"); //keysha
        dup.addParam(CISConstants.ORDER_ID_PARAM, "okn90"); //steak lunch that already exists
        dup.addParam(CISConstants.ITEM_ID_PARAM, "6a6a"); //steak lunch
        dup.addParam(CISConstants.ORDER_TYPE_PARAM, "lunch"); //steak lunch
        success = runTest(d33, true, "error");
        total++;
        if (success)
        {
            passed++;
        }

        //missing parameter for order ID
        Request param = new Request(CISConstants.PLACE_ORDER);
        param.addParam(CISConstants.USER_ID_PARAM, "90abnm"); //keysha
        param.addParam(CISConstants.ITEM_ID_PARAM, "6a6a"); //steak lunch
        param.addParam(CISConstants.ORDER_TYPE_PARAM, "lunch"); //steak lunch
        success = runTest(param, true, "error");
        total++;
        if (success)
        {
            passed++;
        }

        //invalid menu item ID(no items with that ID have been added)
        Request p = new Request(CISConstants.PLACE_ORDER);
        p.addParam(CISConstants.USER_ID_PARAM, "90abnm"); //keysha
        dup.addParam(CISConstants.ORDER_ID_PARAM, "mlp99"); //random order id
        p.addParam(CISConstants.ITEM_ID_PARAM, "9393"); //doesn't exist
        p.addParam(CISConstants.ORDER_TYPE_PARAM, "snack"); //steak lunch
        success = runTest(p, true, "error");
        total++;
        if (success)
        {
            passed++;
        }
//
//
//
//
//
        println("=== GET ITEM ===\n");

        //Place bid for one meal, meal id: 6a6a highest bid is now 155
        Request r1 = new Request(CISConstants.GET_ITEM);
        r1.addParam(CISConstants.ITEM_ID_PARAM, "33a");
        String itemString = "MenuItem{name='Burger', " +
                "description='Cheesey and delicious. " +
                "Beyond meat option available.', price=22.0, id='33a', " +
                "amountAvailable=9, type='breakfast'}";
        success = runTest(r1, false, itemString);
        total++;
        if (success)
        {
            passed++;
        }

        Request r2 = new Request(CISConstants.GET_ITEM);
        r2.addParam(CISConstants.ITEM_ID_PARAM, "6a6a");
        String itemString2 = "MenuItem{name='Steak', " +
                "description='Ribeye steak with veggies and chimichurri', " +
                "price=19.0, id='6a6a', amountAvailable=8, type='lunch'}";
        success = runTest(r2, false, itemString2);
        total++;
        if (success)
        {
            passed++;
        }

        println("=== GET USER ===\n");

        Request u2 = new Request(CISConstants.GET_USER);
        u2.addParam(CISConstants.USER_ID_PARAM, "qwert5");
        String itemString3 = "CISUser{userID='qwert5', name='latisha', " +
                "yearLevel='y13', orders= Order{itemID='33a', " +
                "type='breakfast', orderID='9078ppp'}, money=28.0}";
        success = runTest(u2, false, itemString3);
        total++;
        if (success)
        {
            passed++;
        }

        println("=== GET ORDER ===\n");

        Request o1 = new Request(CISConstants.GET_ORDER);
        o1.addParam(CISConstants.USER_ID_PARAM, "abcd");
        o1.addParam(CISConstants.ORDER_ID_PARAM, "okn90");
        String itemString4 = "Order{itemID='6a6a', type='lunch', orderID='okn90'}";
        success = runTest(o1, false, itemString4);
        total++;
        if (success)
        {
            passed++;
        }

        //GET_CART is tests by students.

        println("=== INVALID COMMANDS ===\n");

        // and what if we send a bad command?
        Request bad = new Request("badCommand");
        success = runTest(bad, true, "");
        total++;
        if (success)
        {
            passed++;
        }

        println("Passed: " + passed + "/" + total);
    }

    /**
     * Runs a request and checks if the result is what was expected (both whether we
     * expect an error and otherwise what String response is expected)
     */
    private boolean runTest(Request request, boolean expectError, String expectedSuccessOutput)
    {
        println(request.toString());
        try
        {
            String result = SimpleClient.makeRequest(HOST, request);
            if (expectError)
            {
                println("Test failed. Expected an error but didn't get one\n");
                return false;
            } else if (!result.equals(expectedSuccessOutput))
            {
                println("Test failed.");
                println("Expected response: " + expectedSuccessOutput);
                println("Actual response:  " + result + "\n");
                return false;
            } else
            {
                println("Test passed.\n");
                return true;
            }
        }
        catch (IOException e)
        {
            if (expectError && e.getMessage().startsWith(SimpleClient.ERROR_KEY))
            {
                println("Test passed.\n");
                return true;
            } else
            {
                println("Test failed. Received unknown error: " + e.getMessage() + "\n");
                return false;
            }
        }
    }
}
