package bif3.tolan.swe1.mcg.database.respositories;

import bif3.tolan.swe1.mcg.model.Card;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CardRepositoryImplementation extends BaseRepository implements CardRepository {
    public CardRepositoryImplementation(Connection connection) {
        super(connection);
    }

    @Override
    public List<Card> getCardById(String cardId) throws SQLException {
        String sql = "SELECT id, name, damage FROM mctg_card WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, cardId);

        ResultSet res = preparedStatement.executeQuery();
        preparedStatement.close();

        return extractUser(res);
    }

    @Override
    public List<Card> getCardStackOfUserByUserId(String userId) {
        return null;
    }

    @Override
    public Card getCardByTradeOfferId(String tradeId) {
        return null;
    }

    @Override
    public List<Card> getCardDeckOfUserByUserId(String userId) {
        return null;
    }

    @Override
    public List<Card> getCardPackageByPackageId(String packageId) {
        return null;
    }

    @Override
    public void addCard(Card card) {

    }

    public Card extractCard(ResultSet res) throws SQLException {
        if (res.first()) {
            String cardId = res.getString("id");
            String cardName = res.getString("name");
            float cardDamage = res.getFloat("damage");

            return new Card();
        } else {
            return null;
        }
    }
}
