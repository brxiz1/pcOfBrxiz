public class Account {
    private String name;
    private String password;
    Account(String name,String psw){
        this.name=name;
        this.password=psw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
