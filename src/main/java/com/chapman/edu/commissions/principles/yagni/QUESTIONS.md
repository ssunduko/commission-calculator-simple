# YAGNI Principle Knowledge Test

## Multiple Choice Questions

1. What does YAGNI stand for in software development?
   a) You Aren't Going to Need It
   b) Yet Another Great New Idea
   c) Yield All Good New Implementations
   d) Your Application Gets Numerous Iterations

2. According to the YAGNI principle, developers should:
   a) Implement features as soon as they think of them
   b) Only implement features when they are actually needed
   c) Always plan for future requirements
   d) Create flexible abstractions for potential future use

3. Which of the following is NOT a common YAGNI violation?
   a) Adding "just in case" functionality
   b) Creating overly generic frameworks
   c) Implementing only what's needed for current requirements
   d) Building complex configuration systems for potential future needs

4. What is the primary goal of the YAGNI principle?
   a) To maximize code flexibility
   b) To reduce complexity and waste
   c) To ensure all possible future scenarios are covered
   d) To create the most generic solution possible

5. Which of the following is a benefit of following the YAGNI principle?
   a) Increased development time
   b) More complex codebase
   c) Reduced maintenance burden
   d) More unused code

## Short Answer Questions

6. Explain how the YAGNI principle relates to the concept of "technical debt" in software development.

7. Describe two specific strategies you can use to avoid YAGNI violations in a project.

8. What are the potential negative consequences of violating the YAGNI principle in a large codebase?

9. How does the YAGNI principle contribute to more efficient development processes?

10. In what situations might it be acceptable to deviate from strict adherence to the YAGNI principle?

## Code Analysis Questions

11. Identify the YAGNI violations in the following code snippet and explain how you would refactor it:
```java
public class User {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private Date birthDate;
    private String phoneNumber;
    private String mobileNumber;
    private String faxNumber;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String nationality;
    private String passportNumber;
    private Date passportExpiryDate;
    private String preferredLanguage;
    private String secondaryLanguage;
    private String tertiaryLanguage;
    private String timezone;
    private String currency;
    private boolean marketingConsent;
    private String referralSource;
    private Date registrationDate;
    private Date lastLoginDate;
    private int loginCount;
    
    // Constructors, getters, setters for all fields
    
    public boolean isActive() {
        return lastLoginDate != null && 
               (new Date().getTime() - lastLoginDate.getTime()) < (90 * 24 * 60 * 60 * 1000);
    }
    
    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        } else {
            return firstName + " " + lastName;
        }
    }
    
    public String getFormattedAddress() {
        StringBuilder address = new StringBuilder();
        address.append(address1);
        if (address2 != null && !address2.isEmpty()) {
            address.append(", ").append(address2);
        }
        address.append(", ").append(city);
        address.append(", ").append(state);
        address.append(" ").append(zipCode);
        address.append(", ").append(country);
        return address.toString();
    }
    
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }
    
    public boolean isEligibleForDiscount() {
        return loginCount > 10 || 
               (registrationDate != null && 
                (new Date().getTime() - registrationDate.getTime()) > (365 * 24 * 60 * 60 * 1000));
    }
    
    public String getPreferredContactMethod() {
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            return "MOBILE";
        } else if (phoneNumber != null && !phoneNumber.isEmpty()) {
            return "PHONE";
        } else {
            return "EMAIL";
        }
    }
}
```

12. How would you apply the YAGNI principle to improve this configuration system?
```java
public class ApplicationConfig {
    private Map<String, Object> configValues = new HashMap<>();
    private List<ConfigChangeListener> listeners = new ArrayList<>();
    private Map<String, List<String>> dependencyGraph = new HashMap<>();
    private Map<String, String> configDescriptions = new HashMap<>();
    private Map<String, Object> defaultValues = new HashMap<>();
    private Map<String, ConfigValidator> validators = new HashMap<>();
    private boolean strictMode = false;
    private String configFilePath;
    private boolean autoReload = false;
    private int reloadInterval = 60; // seconds
    private Timer reloadTimer;
    private ConfigPersister persister;
    private ConfigEncryptor encryptor;
    private boolean encryptionEnabled = false;
    private String encryptionKey;
    private List<String> encryptedKeys = new ArrayList<>();
    private Map<String, ConfigAccessLevel> accessLevels = new HashMap<>();
    private ConfigAuditor auditor;
    private boolean auditingEnabled = false;
    
    public ApplicationConfig(String configFilePath) {
        this.configFilePath = configFilePath;
        this.persister = new FileConfigPersister();
        this.encryptor = new AESConfigEncryptor();
        this.auditor = new DefaultConfigAuditor();
        loadConfig();
    }
    
    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }
    
    public void setAutoReload(boolean autoReload, int reloadInterval) {
        this.autoReload = autoReload;
        this.reloadInterval = reloadInterval;
        if (autoReload) {
            startAutoReload();
        } else {
            stopAutoReload();
        }
    }
    
    public void setEncryption(boolean enabled, String key) {
        this.encryptionEnabled = enabled;
        this.encryptionKey = key;
        if (enabled) {
            encryptor.setKey(key);
            reEncryptValues();
        }
    }
    
    public void setAuditing(boolean enabled) {
        this.auditingEnabled = enabled;
    }
    
    public void addEncryptedKey(String key) {
        encryptedKeys.add(key);
        if (encryptionEnabled && configValues.containsKey(key)) {
            Object value = configValues.get(key);
            if (value instanceof String) {
                configValues.put(key, encryptor.encrypt((String) value));
            }
        }
    }
    
    public void setAccessLevel(String key, ConfigAccessLevel level) {
        accessLevels.put(key, level);
    }
    
    public void addDependency(String key, String dependsOn) {
        if (!dependencyGraph.containsKey(key)) {
            dependencyGraph.put(key, new ArrayList<>());
        }
        dependencyGraph.get(key).add(dependsOn);
    }
    
    public void setDescription(String key, String description) {
        configDescriptions.put(key, description);
    }
    
    public void setDefaultValue(String key, Object value) {
        defaultValues.put(key, value);
        if (!configValues.containsKey(key)) {
            configValues.put(key, value);
        }
    }
    
    public void setValidator(String key, ConfigValidator validator) {
        validators.put(key, validator);
    }
    
    public void setValue(String key, Object value) {
        if (auditingEnabled) {
            auditor.logChange(key, configValues.get(key), value);
        }
        
        if (validators.containsKey(key) && !validators.get(key).isValid(value)) {
            if (strictMode) {
                throw new IllegalArgumentException("Invalid value for " + key);
            } else {
                return;
            }
        }
        
        Object oldValue = configValues.get(key);
        if (encryptionEnabled && encryptedKeys.contains(key) && value instanceof String) {
            configValues.put(key, encryptor.encrypt((String) value));
        } else {
            configValues.put(key, value);
        }
        
        for (ConfigChangeListener listener : listeners) {
            listener.onConfigChanged(key, oldValue, value);
        }
        
        saveConfig();
    }
    
    public Object getValue(String key) {
        if (!configValues.containsKey(key)) {
            if (defaultValues.containsKey(key)) {
                return defaultValues.get(key);
            }
            if (strictMode) {
                throw new IllegalArgumentException("Config key not found: " + key);
            }
            return null;
        }
        
        Object value = configValues.get(key);
        if (encryptionEnabled && encryptedKeys.contains(key) && value instanceof String) {
            return encryptor.decrypt((String) value);
        }
        return value;
    }
    
    public void addChangeListener(ConfigChangeListener listener) {
        listeners.add(listener);
    }
    
    public void removeChangeListener(ConfigChangeListener listener) {
        listeners.remove(listener);
    }
    
    private void loadConfig() {
        Map<String, Object> loaded = persister.load(configFilePath);
        if (loaded != null) {
            configValues.putAll(loaded);
        }
    }
    
    private void saveConfig() {
        persister.save(configFilePath, configValues);
    }
    
    private void startAutoReload() {
        if (reloadTimer != null) {
            reloadTimer.cancel();
        }
        reloadTimer = new Timer(true);
        reloadTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                loadConfig();
            }
        }, reloadInterval * 1000, reloadInterval * 1000);
    }
    
    private void stopAutoReload() {
        if (reloadTimer != null) {
            reloadTimer.cancel();
            reloadTimer = null;
        }
    }
    
    private void reEncryptValues() {
        for (String key : encryptedKeys) {
            if (configValues.containsKey(key) && configValues.get(key) instanceof String) {
                String decrypted = encryptor.decrypt((String) configValues.get(key));
                configValues.put(key, encryptor.encrypt(decrypted));
            }
        }
    }
    
    public interface ConfigChangeListener {
        void onConfigChanged(String key, Object oldValue, Object newValue);
    }
    
    public interface ConfigValidator {
        boolean isValid(Object value);
    }
    
    public interface ConfigPersister {
        Map<String, Object> load(String path);
        void save(String path, Map<String, Object> config);
    }
    
    public interface ConfigEncryptor {
        void setKey(String key);
        String encrypt(String value);
        String decrypt(String encryptedValue);
    }
    
    public interface ConfigAuditor {
        void logChange(String key, Object oldValue, Object newValue);
    }
    
    public enum ConfigAccessLevel {
        READ_ONLY, READ_WRITE, ADMIN_ONLY
    }
    
    private class FileConfigPersister implements ConfigPersister {
        @Override
        public Map<String, Object> load(String path) {
            // Implementation for loading from file
            return new HashMap<>();
        }
        
        @Override
        public void save(String path, Map<String, Object> config) {
            // Implementation for saving to file
        }
    }
    
    private class AESConfigEncryptor implements ConfigEncryptor {
        @Override
        public void setKey(String key) {
            // Set encryption key
        }
        
        @Override
        public String encrypt(String value) {
            // Encrypt value
            return "encrypted_" + value;
        }
        
        @Override
        public String decrypt(String encryptedValue) {
            // Decrypt value
            if (encryptedValue.startsWith("encrypted_")) {
                return encryptedValue.substring(10);
            }
            return encryptedValue;
        }
    }
    
    private class DefaultConfigAuditor implements ConfigAuditor {
        @Override
        public void logChange(String key, Object oldValue, Object newValue) {
            // Log configuration changes
        }
    }
}
```