package recommentTest;

/**
 * 电影
 */
public class Movies {
    private Integer movieId;
    private String title;
    private String genres;

    public Movies(Integer movieId,String genres){
        this.movieId = movieId;
        this.title = "111";
        this.genres = genres;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
