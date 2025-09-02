package com.chapman.edu.commissions.documentation.naming.original;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * User class
 */
public class User {
    private String id;
    private String usr;
    private String email;
    private String fn;
    private String ln;
    private String pwd;
    private Set<UserRole> r;
    private boolean a;
    private LocalDateTime ll;
    private LocalDate cd;
    private String cb;
    private String mgr;
    private String dept;
    private String terr;
    
    public User() {
        this.r = new HashSet<>();
        this.a = true;
        this.cd = LocalDate.now();
    }
    
    public User(String usr, String email, String fn, String ln) {
        this();
        this.usr = usr;
        this.email = email;
        this.fn = fn;
        this.ln = ln;
    }
    
    // Getters and setters with poor naming
    
    public String getId() {
        return id;
    }

    public LocalDate getCd() {return this.cd; }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsr() {
        return usr;
    }
    
    public void setUsr(String usr) {
        this.usr = usr;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFn() {
        return fn;
    }
    
    public void setFn(String fn) {
        this.fn = fn;
    }
    
    public String getLn() {
        return ln;
    }
    
    public void setLn(String ln) {
        this.ln = ln;
    }
    
    public Set<UserRole> getR() {
        return r;
    }
    
    public void setR(Set<UserRole> r) {
        this.r = r;
    }
    
    public void add(UserRole role) {
        this.r.add(role);
    }
    
    public boolean has(UserRole role) {
        return this.r.contains(role);
    }
    
    public boolean getA() {
        return a;
    }
    
    public void setA(boolean a) {
        this.a = a;
    }
    
    public String getName() {
        return fn + " " + ln;
    }
    
    public boolean check() {
        return has(UserRole.SR);
    }
    
    public boolean check2() {
        return has(UserRole.SM);
    }
    
    public boolean validate() {
        return has(UserRole.FA);
    }
    
    public boolean process() {
        return has(UserRole.SA);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}