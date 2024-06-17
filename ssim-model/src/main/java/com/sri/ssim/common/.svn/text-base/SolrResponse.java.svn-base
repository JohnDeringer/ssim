package com.sri.ssim.common;

/**
 * @author <a href="mailto:john.deringer@sri.com">John Deringer</a>
 *         Date: 1/14/13
 */
public class SolrResponse {

    private String searchItem;
    private Long artifactId;
   //  private int count;

    public String getSearchItem() {
        return searchItem;
    }

    public void setSearchItem(String searchItem) {
        this.searchItem = searchItem;
    }

    public Long getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(Long artifactId) {
        this.artifactId = artifactId;
    }

//    public int getCount() {
//        return count;
//    }
//
//    public void setCount(int count) {
//        this.count = count;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SolrResponse that = (SolrResponse) o;

        if (artifactId != null ? !artifactId.equals(that.artifactId) : that.artifactId != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return artifactId != null ? artifactId.hashCode() : 0;
    }

}
