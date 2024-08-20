package cn.taosx.btcplugin.entity;

/**
 * Created by taosx on 2024-08-20 10:38.
 */

public class ToolWindowDataEntity {
    private String cryptoName;
    private String price;

    public String getCryptoName() {
        return cryptoName;
    }

    public void setCryptoName(String cryptoName) {
        this.cryptoName = cryptoName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
