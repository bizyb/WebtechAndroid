package bizu.work.placessearch;

public class CustomLocation {

    private SearchFragment sfInstance;
    public CustomLocation(SearchFragment sf) {

        this.sfInstance = sf;
    }

    public void setLocationListener() {
        sfInstance.setCurLocation(-1,1.0);
    }
}
