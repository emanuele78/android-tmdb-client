package com.example.android.popularmovies2.model;

import android.support.annotation.NonNull;

/**
 * Created by Emanuele on 08/03/2018.
 */
public class LinkedPerson implements Comparable<LinkedPerson> {

    private String profilePic;
    private int personId;
    private String name;

    public LinkedPerson(String profilePic, int personId, String name) {
        this.profilePic = profilePic;
        this.personId = personId;
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getName() {
        return name;
    }

    public int getPersonId() {
        return personId;
    }

    @Override
    public int compareTo(@NonNull LinkedPerson linkedPerson) {
        if (this.personId < linkedPerson.getPersonId()) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LinkedPerson)) {
            return false;
        }
        LinkedPerson that = (LinkedPerson) o;
        return getPersonId() == that.getPersonId();
    }

    @Override
    public int hashCode() {
        return getPersonId();
    }
}
