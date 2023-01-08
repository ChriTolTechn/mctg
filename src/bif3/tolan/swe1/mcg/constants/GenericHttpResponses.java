package bif3.tolan.swe1.mcg.constants;

import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;

public final class GenericHttpResponses {
    public final static HttpResponse INTERNAL_ERROR = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpContentType.PLAIN_TEXT, "An issue occured on server side. Please contact the support for further instructions.");
    public final static HttpResponse WRONG_CREDENTIALS = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.PLAIN_TEXT, "Wrong user credentials. Please try again.");
    public final static HttpResponse INVALID_PATH = new HttpResponse(HttpStatus.NOT_FOUND, HttpContentType.PLAIN_TEXT, "Invalid path. Please check that you entered the path correctly.");
    public final static HttpResponse NOT_LOGGED_IN = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.PLAIN_TEXT, "You are not logged in. Please log in to perform this action.");
    public final static HttpResponse INVALID_INPUT = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "The data you tried to pass does not match the requirements. Please try again.");
    public final static HttpResponse IDENTICAL_USER = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "You cannot perform this action with yourself.");
    public final static HttpResponse BATTLE_REQUEST_TIMEOUT = new HttpResponse(HttpStatus.GATEWAY_TIMEOUT, HttpContentType.PLAIN_TEXT, "Requested timed out, no user found to battle. Please try again later.");
    public final static HttpResponse INVALID_DECK = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.PLAIN_TEXT, "The deck your are trying to use is invalid. Please ensure that it has 4 cards.");
    public final static HttpResponse ITEM_NOT_OWNED = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.PLAIN_TEXT, "The items you are trying to interact with do no belong to you. Please try again with your own values");
    public final static HttpResponse SUCCESS_UPDATE = new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, "Successfully updated!");
    public final static HttpResponse SUCCESS_CREATE = new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, "Successfully created!");
    public final static HttpResponse UNAUTHORIZED = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.PLAIN_TEXT, "You are not authorized to perform this action!");
    public final static HttpResponse ID_EXISTS = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "An item with the provided IDs already exists. Please enter a different ID.");
    public final static HttpResponse UNSUPPORTED_CARD_TYPE = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "One of the types specified in a card does not exist in the system. Please specify a valid one.");
    public final static HttpResponse UNSUPPORTED_CARD_ELEMENT = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "One of the elements specified in a card does not exit in the system. Please specify a valid one.");
    public final static HttpResponse SUCCESS_BUY = new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, "Purchase successfull!");
    public final static HttpResponse NOT_ENOUGH_COINS = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "You don't have enough coins to buy this!");
    public final static HttpResponse NOT_AVAILABLE_FOR_PURCHASE = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.PLAIN_TEXT, "Currently there is nothing available to purchase. Please try again later.");
    public final static HttpResponse NO_ACTIVE_TRADES = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.PLAIN_TEXT, "There are currently no active trades. Check again later!");
    public final static HttpResponse SUCCESS_TRADE = new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, "Trade successful!");
    public final static HttpResponse TRADE_CRITERIA_NOT_MET = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "Your card does not meet the trade criterias. Please try with a different card.");
    public final static HttpResponse TRADE_NOT_FOUND = new HttpResponse(HttpStatus.NOT_FOUND, HttpContentType.PLAIN_TEXT, "The trade you are looking for does not exist");
    public final static HttpResponse HAS_ACTIVE_TRADE = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.PLAIN_TEXT, "You can only have one trade offer at the same time active. Delete the old one to create a new one");
    public final static HttpResponse SUCCESS_DELETE = new HttpResponse(HttpStatus.OK, HttpContentType.PLAIN_TEXT, "Successfully deleted!");
    public final static HttpResponse USER_EXISTS = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.PLAIN_TEXT, "A user with this username already exists. Please pick another one.");
    public final static HttpResponse INVALID_TOKEN = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.PLAIN_TEXT, "Either you have provided no authentication token or it is invalid. Please provide a valid one to perform this action.");
}
