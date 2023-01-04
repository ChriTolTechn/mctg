package bif3.tolan.swe1.mcg.workers;

import bif3.tolan.swe1.mcg.exceptions.DuplicateItemException;
import bif3.tolan.swe1.mcg.exceptions.InvalidCardParameterException;
import bif3.tolan.swe1.mcg.httpserver.ContentType;
import bif3.tolan.swe1.mcg.httpserver.HttpRequest;
import bif3.tolan.swe1.mcg.httpserver.HttpResponse;
import bif3.tolan.swe1.mcg.httpserver.HttpStatus;
import bif3.tolan.swe1.mcg.model.Card;
import bif3.tolan.swe1.mcg.utils.CardUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardWorker extends BaseWorker {
    @Override
    public HttpResponse executeRequest(HttpRequest request) {
        return null;
    }

    private HttpResponse addNewPackage(HttpRequest request) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = request.getBody();

        //TODO change
        return new HttpResponse(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Unknown path");
    }

    private Card createNewCard(String cardId, String cardName, String cardDamage) throws InvalidCardParameterException, DuplicateItemException {
        if (checkIfCardWithIdExists(cardId))
            throw new DuplicateItemException();
        if (cardName != null && cardDamage != null) {
            try {
                float cardDamageAsFloat = Float.parseFloat(cardDamage);
                return CardUtils.buildCard(cardId, cardName, cardDamageAsFloat);
            } catch (NumberFormatException e) {
            }
        }
        throw new InvalidCardParameterException();
    }

    private boolean checkIfCardWithIdExists(String cardId) {
        //TODO
        return true;
    }
}
