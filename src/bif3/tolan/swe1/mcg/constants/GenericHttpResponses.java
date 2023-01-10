package bif3.tolan.swe1.mcg.constants;

import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpContentType;
import bif3.tolan.swe1.mcg.httpserver.enums.HttpStatus;

public final class GenericHttpResponses {
    private final static String DEFAULT_JSON_FORMAT = "{\"message\":\"%s\"}";
    public final static HttpResponse INTERNAL_ERROR = new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "An issue occured on server side. Please contact the support for further instructions."));
    public final static HttpResponse WRONG_CREDENTIALS = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Wrong user credentials. Please try again."));
    public final static HttpResponse INVALID_PATH = new HttpResponse(HttpStatus.NOT_FOUND, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Invalid path. Please check that you entered the path correctly."));
    public final static HttpResponse INVALID_INPUT = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "The data you tried to pass does not match the requirements. Please try again."));
    public final static HttpResponse IDENTICAL_USER = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "You cannot perform this action with yourself."));
    public final static HttpResponse BATTLE_REQUEST_TIMEOUT = new HttpResponse(HttpStatus.GATEWAY_TIMEOUT, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Requested timed out, no user found to battle. Please try again later."));
    public final static HttpResponse INVALID_DECK = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "The deck your are trying to use is invalid. Please ensure that it has 4 cards."));
    public final static HttpResponse ITEM_NOT_OWNED = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "The items you are trying to interact with do no belong to you. Please try again with your own values"));
    public final static HttpResponse SUCCESS_UPDATE = new HttpResponse(HttpStatus.OK, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Successfully updated!"));
    public final static HttpResponse UNAUTHORIZED = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "You are not authorized to perform this action!"));
    public final static HttpResponse ID_EXISTS = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "An item with the provided IDs already exists. Please enter a different ID."));
    public final static HttpResponse UNSUPPORTED_CARD_TYPE = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "One of the types specified in a card does not exist in the system. Please specify a valid one."));
    public final static HttpResponse UNSUPPORTED_CARD_ELEMENT = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "One of the elements specified in a card does not exit in the system. Please specify a valid one."));
    public final static HttpResponse SUCCESS_BUY = new HttpResponse(HttpStatus.OK, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Purchase successfull!"));
    public final static HttpResponse NOT_ENOUGH_COINS = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "You don't have enough coins to buy this!"));
    public final static HttpResponse NOT_AVAILABLE_FOR_PURCHASE = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Currently there is nothing available to purchase. Please try again later."));
    public final static HttpResponse NO_ACTIVE_TRADES = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "There are currently no active trades. Check again later!"));
    public final static HttpResponse SUCCESS_TRADE = new HttpResponse(HttpStatus.OK, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Trade successful!"));
    public final static HttpResponse TRADE_CRITERIA_NOT_MET = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Your card does not meet the trade criterias. Please try with a different card."));
    public final static HttpResponse TRADE_NOT_FOUND = new HttpResponse(HttpStatus.NOT_FOUND, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "The trade you are looking for does not exist"));
    public final static HttpResponse HAS_ACTIVE_TRADE = new HttpResponse(HttpStatus.UNAUTHORIZED, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "You can only have one trade offer at the same time active. Delete the old one to create a new one"));
    public final static HttpResponse SUCCESS_DELETE = new HttpResponse(HttpStatus.OK, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Successfully deleted!"));
    public final static HttpResponse USER_EXISTS = new HttpResponse(HttpStatus.NOT_ACCEPTABLE, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "A user with this username already exists. Please pick another one."));
    public final static HttpResponse INVALID_TOKEN = new HttpResponse(HttpStatus.BAD_REQUEST, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Either you have provided no authentication token or it is invalid. Please provide a valid one to perform this action."));
    public final static HttpResponse NO_DATA = new HttpResponse(HttpStatus.OK, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "There is currently no data available"));
    public final static HttpResponse SUCCESS_CREATE = new HttpResponse(HttpStatus.OK, HttpContentType.JSON, String.format(DEFAULT_JSON_FORMAT, "Successfully created!"));
}
