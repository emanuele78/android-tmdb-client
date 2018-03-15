package com.example.android.popularmovies2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Emanuele Mazzante on 25/02/2018.
 */
public class ProductionCompany implements Parcelable {

    @SerializedName("name")
    private String productionCompanyName;
    @SerializedName("id")
    private int productionCompanyId;

    @Override
    public int describeContents() {
        return 0;
    }

    public String getProductionCompanyName() {
        return productionCompanyName;
    }

    public int getProductionCompanyId() {
        return productionCompanyId;
    }

    public ProductionCompany(String name, int id) {
        this.productionCompanyName = name;
        this.productionCompanyId = id;
    }

    public void setProductionCompanyName(String productionCompanyName) {
        this.productionCompanyName = productionCompanyName;
    }

    public void setProductionCompanyId(int productionCompanyId) {
        this.productionCompanyId = productionCompanyId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.productionCompanyName);
        dest.writeInt(this.productionCompanyId);
    }

    public ProductionCompany(String name) {
        productionCompanyName = name;
    }

    protected ProductionCompany(Parcel in) {
        this.productionCompanyName = in.readString();
        this.productionCompanyId = in.readInt();
    }

    public static final Parcelable.Creator<ProductionCompany> CREATOR =
            new Parcelable.Creator<ProductionCompany>() {
                @Override
                public ProductionCompany createFromParcel(Parcel source) {
                    return new ProductionCompany(source);
                }

                @Override
                public ProductionCompany[] newArray(int size) {
                    return new ProductionCompany[size];
                }
            };
}
