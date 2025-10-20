#!/bin/bash
# Script to fix all compilation errors in integration package

# Fix H2UserRepository - change getPassword() to getPasswordHash()
sed -i 's/user\.getPassword()/user.getPasswordHash()/g' src/main/java/com/chapman/edu/commissions/integration/repository/H2UserRepository.java
sed -i 's/user\.setPassword(/user.setPasswordHash(/g' src/main/java/com/chapman/edu/commissions/integration/repository/H2UserRepository.java
sed -i 's/setRoles(roles)/setRoles(new java.util.HashSet<>(roles))/g' src/main/java/com/chapman/edu/commissions/integration/repository/H2UserRepository.java
sed -i 's/setRoles(new ArrayList<>())/setRoles(new java.util.HashSet<>())/g' src/main/java/com/chapman/edu/commissions/integration/repository/H2UserRepository.java

# Fix UserService
sed -i 's/user\.getPassword()/user.getPasswordHash()/g' src/main/java/com/chapman/edu/commissions/integration/service/UserService.java
sed -i 's/user\.setPassword(/user.setPasswordHash(/g' src/main/java/com/chapman/edu/commissions/integration/service/UserService.java
sed -i 's/List\.of(UserRole\./java.util.Set.of(UserRole./g' src/main/java/com/chapman/edu/commissions/integration/service/UserService.java

# Fix IntegrationApplication
sed -i 's/\.setPassword(/.setPasswordHash(/g' src/main/java/com/chapman/edu/commissions/integration/IntegrationApplication.java
sed -i 's/List\.of(UserRole\./java.util.Set.of(UserRole./g' src/main/java/com/chapman/edu/commissions/integration/IntegrationApplication.java
sed -i 's/UserRole\.MANAGER/UserRole.SALES_MANAGER/g' src/main/java/com/chapman/edu/commissions/integration/IntegrationApplication.java

# Fix DealProduct constructor calls - need to add productId
sed -i 's/new DealProduct("\([^"]*\)", new BigDecimal("\([^"]*\)"), \([0-9]*\))/new DealProduct("PROD-\1", "\1", \3, new BigDecimal("\2"))/g' src/main/java/com/chapman/edu/commissions/integration/IntegrationApplication.java

# Fix IntegrationApplicationTest
sed -i 's/\.setPassword(/.setPasswordHash(/g' src/test/java/com/chapman/edu/commissions/integration/IntegrationApplicationTest.java
sed -i 's/List\.of(UserRole\./java.util.Set.of(UserRole./g' src/test/java/com/chapman/edu/commissions/integration/IntegrationApplicationTest.java
sed -i 's/UserRole\.MANAGER/UserRole.SALES_MANAGER/g' src/test/java/com/chapman/edu/commissions/integration/IntegrationApplicationTest.java
sed -i 's/new DealProduct("\([^"]*\)", new BigDecimal("\([^"]*\)"), \([0-9]*\))/new DealProduct("PROD-\1", "\1", \3, new BigDecimal("\2"))/g' src/test/java/com/chapman/edu/commissions/integration/IntegrationApplicationTest.java

# Fix H2DealRepository JSON deserialization
sed -i 's/JsonHelper\.fromJson(.*productsJson.*DealProduct\[\]\.class);/JsonHelper.fromJson(productsJson, DealProduct[].class);/g' src/main/java/com/chapman/edu/commissions/integration/repository/H2DealRepository.java
sed -i 's/deal\.setProducts(List\.of(products));/deal.setProducts(java.util.Arrays.asList(products));/g' src/main/java/com/chapman/edu/commissions/integration/repository/H2DealRepository.java

echo "Fixed all compilation errors!"