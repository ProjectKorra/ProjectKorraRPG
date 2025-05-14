package com.projectkorra.rpg.modules.worldevents.schedule.storage;

import com.projectkorra.projectkorra.storage.DBConnection;
import com.projectkorra.projectkorra.storage.MySQL;
import com.projectkorra.rpg.ProjectKorraRPG;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.logging.Level;

public class ScheduleStorage extends DBConnection {
    /**
     * Gets the last time a specific world event was triggered.
     * 
     * @param worldEventKey The unique key for the world event
     * @return Optional containing the last trigger time, or empty if never triggered
     * @throws SQLException if a database error occurs
     */
    public Optional<Instant> getLastTriggeredTime(String worldEventKey) throws SQLException {
        try {
            Connection conn = sql.getConnection();
            
            if (conn == null) {
                throw new SQLException("Could not get database connection");
            }
            
            final String query = "SELECT last_triggered FROM " + TABLE_NAME + " WHERE worldevent = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, worldEventKey);

            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Timestamp timestamp = rs.getTimestamp("last_triggered");
                return Optional.of(timestamp.toInstant());
            }

            // If there's no last trigger time, return empty
            return Optional.empty();
        } catch (SQLException e) {
            ProjectKorraRPG.getPlugin().getLogger().log(Level.SEVERE, "Failed to get last triggered time for event: " + worldEventKey, e);
            throw e;
        }
    }
    
    /**
     * Updates the last triggered time for a specific world event.
     * Creates a new record if the world event doesn't exist in the database.
     * 
     * @param worldEventKey The unique key for the world event
     * @param triggerTime The time when the event was triggered (null for current time)
     * @throws SQLException if a database error occurs
     */
    public void updateLastTriggeredTime(String worldEventKey, Instant triggerTime) throws SQLException {
        Instant timeToRecord = (triggerTime != null) ? triggerTime : Instant.now();
        PreparedStatement updatePs;
        
        try {
            Connection conn = sql.getConnection();
            
            if (conn == null) {
                throw new SQLException("Could not get database connection");
            }
            
            // First, check if the event record exists
            PreparedStatement checkPs = conn.prepareStatement("SELECT id FROM " + TABLE_NAME + " WHERE worldevent = ?");
            checkPs.setString(1, worldEventKey);
            ResultSet rs = checkPs.executeQuery();
            
            boolean recordExists = rs.next();
            
            // Close the result set and first statement before creating another
            rs.close();
            checkPs.close();
            
            if (recordExists) {
                // Update existing record
                updatePs = conn.prepareStatement("UPDATE " + TABLE_NAME + " SET last_triggered = ? WHERE worldevent = ?");
                updatePs.setTimestamp(1, Timestamp.from(timeToRecord));
                updatePs.setString(2, worldEventKey);

                updatePs.executeUpdate();
            } else {
                // Insert a new record
                updatePs = conn.prepareStatement("INSERT INTO " + TABLE_NAME + " (worldevent, last_triggered) VALUES (?, ?)");
                updatePs.setString(1, worldEventKey);
                updatePs.setTimestamp(2, Timestamp.from(timeToRecord));

                updatePs.executeUpdate();
            }
        } catch (SQLException e) {
            ProjectKorraRPG.getPlugin().getLogger().log(Level.SEVERE,"Failed to update last triggered time for event: " + worldEventKey, e);
            throw e;
        }
    }
}
