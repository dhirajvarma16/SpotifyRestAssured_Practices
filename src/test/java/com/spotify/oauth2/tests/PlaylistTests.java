package com.spotify.oauth2.tests;

import com.spotify.oauth2.api.StatusCode;
import com.spotify.oauth2.api.applicationApi.PlaylistApi;
import com.spotify.oauth2.pojo.Error;
import com.spotify.oauth2.pojo.Playlist;
import static com.spotify.oauth2.api.SpecBuilder.*;

import com.spotify.oauth2.utils.DataLoader;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static com.spotify.oauth2.utils.FakerUtils.generateDescription;
import static com.spotify.oauth2.utils.FakerUtils.generateName;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Spotify Oauth 2.0")
@Feature("Playlist API")
public class PlaylistTests extends BaseTest{

    @Step
    public Playlist playlistBuilder(String name, String description, boolean _public)
    {
        return Playlist.builder().
                name(name).
                description(description).
                _public(_public).
                build();

    }
    @Step
    public void assetPlaylistEqual(Playlist responsePlaylist, Playlist requestPlaylist){
        assertThat(responsePlaylist.getName() ,equalTo(requestPlaylist.getName()));
        assertThat(responsePlaylist.getDescription() ,equalTo(requestPlaylist.getDescription()));
        assertThat(responsePlaylist.get_public() ,equalTo(requestPlaylist.get_public()));

    }

    @Step
    public void assertStatusCode(int actualStatusCode, StatusCode statusCode){
        assertThat(actualStatusCode,equalTo(statusCode.code));
    }

    @Step
    public void assertErrorVerification(Error responseErr, StatusCode statusCode){
     assertThat(responseErr.getError().getStatus(),equalTo(statusCode.code));
     assertThat(responseErr.getError().getMessage(), equalTo(statusCode.msg));
    }

    @Test
    public  void ShouldBeAbleToCreateAPlaylist(){
        Playlist requestPlaylist = playlistBuilder(generateName(),generateDescription(),false);
        Response response= PlaylistApi.post(requestPlaylist);
        assertStatusCode(response.statusCode(),StatusCode.CODE_201);
        assetPlaylistEqual(response.as(Playlist.class),requestPlaylist);

        }
     @Story("Create a playlist story")
    @Link("https://example.org")
    @Link(name = "allure", type = "mylink")
    @TmsLink("12345")
    @Issue("1234567")
    @Description("this is the description")
    @Test(description = "Should be able to create a playlist")
    public void ShouldBeAbleToGetAPlaylist(){
        Playlist requestPlaylist = playlistBuilder("Upload playlist Name","Upload playlist description",false);
        Response response =PlaylistApi.get(DataLoader.getInstance().getPlaylistId());
         assertStatusCode(response.statusCode(),StatusCode.CODE_200);
        Playlist resPlaylist=response.as(Playlist.class);
        assertThat(requestPlaylist.getName(),equalTo(requestPlaylist.getName()));
        assertThat(requestPlaylist.getDescription(),equalTo(requestPlaylist.getDescription()));
    }

    @Test
    public void ShouldBeAbleToUpdateAPlaylist(){
        Playlist requestPlaylist = playlistBuilder(generateName(),generateDescription(),false);
        Response response =PlaylistApi.update(DataLoader.getInstance().getUpdatePlaylistId(), requestPlaylist);
        assertStatusCode(response.statusCode(),StatusCode.CODE_200);
    }

    @Test
    public void ShouldNotBeAbleToCreateAPlaylistWithName(){
        Playlist requestPlaylist = playlistBuilder("",generateDescription(),false);
        Response response= PlaylistApi.post(requestPlaylist);
        assertErrorVerification(response.as(Error.class),StatusCode.CODE_400);

    }

    @Test
    public void ShouldNotBeAbleToCreateAPlaylistWithExpiredToken(){
        String invalid_token ="12345";
        Playlist requestPlaylist = playlistBuilder(generateName(),generateDescription(), false);
        Response response =PlaylistApi.post(invalid_token,requestPlaylist);
        assertErrorVerification(response.as(Error.class),StatusCode.CODE_401);
     }

}
