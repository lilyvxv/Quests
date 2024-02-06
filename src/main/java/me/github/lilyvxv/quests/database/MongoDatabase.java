package me.github.lilyvxv.quests.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import me.github.lilyvxv.quests.structs.PlayerInfo;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import static me.github.lilyvxv.quests.Quests.logger;

public class MongoDatabase {

    private final String databaseName;
    private final String collectionName;
    private MongoCollection<Document> mongoCollection;

    public MongoDatabase(String connectionString, String databaseName, String collectionName) {
        this.databaseName = databaseName;
        this.collectionName = collectionName;

        // Build the database connection and connect to it
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        MongoClient mongoClient;
        try {
            mongoClient = MongoClients.create(settings);
        } catch (Exception e) {
            logger.warning("Error creating MongoClient: " + e);
            throw new RuntimeException("MongoClient initialization error", e);
        }

        // Set up our database and collection
        setupDatabaseAsync(mongoClient);
    }

    public void createPlayerRecordAsync(Player player) {
        // Create a new blank player record using their UUID
        UUID playerUUID = player.getUniqueId();

        CompletableFuture.runAsync(() -> {
            PlayerInfo playerInfo = new PlayerInfo(playerUUID, new ArrayList<>(), new ArrayList<>());
            List<Document> documents = Collections.singletonList(Converters.playerInfoToDocument(playerInfo));
            mongoCollection.insertMany(documents);
        });
    }

    public CompletableFuture<PlayerInfo> fetchPlayerRecordAsync(Player player) {
        // Fetch player's record using their UUID
        return CompletableFuture.supplyAsync(() -> {
            UUID playerUUID = player.getUniqueId();
            Document collectionQuery = new Document("playerUUID", playerUUID.toString());
            FindIterable<Document> queryResult = mongoCollection.find(collectionQuery);
            Document playerRecord = queryResult.first();

            if (playerRecord != null) {
                return Converters.playerInfoFromDocument(playerRecord);
            }

            return null;
        });
    }

    public CompletableFuture<Void> updatePlayerRecordAsync(PlayerInfo playerInfo) {
        return CompletableFuture.runAsync(() -> {
            UUID playerUUID = playerInfo.playerUUID;
            Document filter = new Document("playerUUID", playerUUID.toString());
            Document update = new Document("$set", Converters.playerInfoToDocument(playerInfo));

            UpdateOptions options = new UpdateOptions();
            mongoCollection.updateOne(filter, update, options);
        });
    }

    public CompletableFuture<Boolean> playerHasRecordAsync(Player player) {
        // Check if a record for a player by UUID exists in the collection
        UUID playerUUID = player.getUniqueId();

        return CompletableFuture.supplyAsync(() -> {
            Document collectionQuery = new Document("playerUUID", playerUUID.toString());
            long documentCount = mongoCollection.countDocuments(collectionQuery);
            return documentCount != 0;
        });
    }

    private void setupDatabaseAsync(MongoClient mongoClient) {
        CompletableFuture.runAsync(() -> {
            try {
                // Find our database and collection
                com.mongodb.client.MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);

                if (!databaseExistsAsync(mongoClient, databaseName).get()) {
                    logger.info(String.format("Database %s does not exist, creating it now", databaseName));
                    mongoDatabase.createCollection(collectionName);
                }

                mongoCollection = mongoDatabase.getCollection(collectionName);

                if (!collectionExistsAsync(mongoDatabase, collectionName).get()) {
                    logger.info(String.format("Collection %s does not exist, creating it now", collectionName));
                    mongoDatabase.createCollection(collectionName);
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.log(Level.SEVERE, "An exception was thrown whilst setting up the database:", e);
            }
        });
    }

    public CompletableFuture<Boolean> databaseExistsAsync(MongoClient mongoClient, String databaseName) {
        return CompletableFuture.supplyAsync(() -> {
            // Check if a collection exists on a client
            for (String dbName : mongoClient.listDatabaseNames()) {
                if (dbName.equals(databaseName)) {
                    return true;
                }
            }
            return false;
        });
    }

    public CompletableFuture<Boolean> collectionExistsAsync(com.mongodb.client.MongoDatabase mongoDatabase, String collectionName) {
        return CompletableFuture.supplyAsync(() -> {
            // Check if a collection exists in a database
            for (String collName : mongoDatabase.listCollectionNames()) {
                if (collName.equals(collectionName)) {
                    return true;
                }
            }
            return false;
        });
    }
}
