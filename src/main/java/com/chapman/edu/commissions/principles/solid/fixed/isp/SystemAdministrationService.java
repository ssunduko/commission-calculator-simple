package com.chapman.edu.commissions.principles.solid.fixed.isp;

/**
 * This interface defines system administration operations.
 * It follows the Interface Segregation Principle by focusing only on system administration,
 * allowing clients to depend only on the methods they need.
 */
public interface SystemAdministrationService {
    
    /**
     * Backs up the database.
     */
    void backupDatabase();
    
    /**
     * Restores the database from a backup.
     * 
     * @param backupId The ID of the backup to restore
     */
    void restoreDatabase(String backupId);
    
    /**
     * Purges old data from the system.
     * 
     * @param olderThan The date threshold for purging (format: YYYY-MM-DD)
     */
    void purgeOldData(String olderThan);
    
    /**
     * Updates a system setting.
     * 
     * @param settingName The name of the setting
     * @param settingValue The new value for the setting
     */
    void updateSystemSettings(String settingName, String settingValue);
    
    /**
     * Gets a system setting.
     * 
     * @param settingName The name of the setting
     * @return The value of the setting
     */
    String getSystemSetting(String settingName);
    
    /**
     * Logs a system activity.
     * 
     * @param activity The activity description
     * @param performedBy The ID of the user who performed the activity
     */
    void logSystemActivity(String activity, String performedBy);
}