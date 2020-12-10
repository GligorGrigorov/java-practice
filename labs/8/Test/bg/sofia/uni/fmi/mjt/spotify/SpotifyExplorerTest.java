package bg.sofia.uni.fmi.mjt.spotify;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpotifyExplorerTest {

    private SpotifyExplorer e;
    private List<SpotifyTrack> allTracks;
    private final String text = """
            id,artists,name,year,popularity,duration_ms,tempo,loudness,valence,acousticness,danceability,energy,liveness,speechiness,explicit
            4BJqT0PrAfrxzMOxytFOIz,['Sergei Rachmaninoff'; 'James Levine'; 'Berliner Philharmoniker'],Piano Concerto No. 3 in D Minor Op. 30: III. Finale. Alla breve,1921,4,831667,80.954,-20.096,0.0594,0.982,0.279,0.211,0.665,0.0366,0
            7xPhfUan2yNtyFG0cUWkt8,['Dennis Day'],Clancy Lowered the Boom,1921,5,180533,60.936,-12.441,0.963,0.732,0.819,0.341,0.16,0.415,0
            1o6I8BglA6ylDMrIELygv1,['KHP Kridhamardawa Karaton Ngayogyakarta Hadiningrat'],Gati Bali,1924,5,500062,110.339,-14.85,0.0394,0.961,0.328,0.166,0.101,0.0339,0
            1vyWKqg4E54w3woxOqIduz,['Bessie Smith'],Work House Blues,1924,0,193960,80.287,-10.61,0.456,0.991,0.494,0.212,0.777,0.0332,0
            4d6HGyGT8e121BsdKmw9v6,['Phil Regan'],When Irish Eyes Are Smiling,1921,2,166693,101.665,-10.096,0.253,0.957,0.418,0.193,0.229,0.038,0
            4pyw9DVHGStUre4J6hPngr,['KHP Kridhamardawa Karaton Ngayogyakarta Hadiningrat'],Gati Mardika,1921,6,395076,119.824,-12.506,0.196,0.579,0.697,0.346,0.13,0.07,0
            5uNZnElqOS3W4fRmRYPk4T,['John McCormack'],The Wearing of the Green,1999,4,159507,66.221,-10.589,0.406,0.996,0.518,0.203,0.115,0.0615,0
            02GDntOXexBFUvSgaXLPkd,['Sergei Rachmaninoff'],Morceaux de fantaisie Op. 3: No. 2 Prélude in C-Sharp Minor. Lento,1999,2,218773,92.867,-21.091,0.0731,0.993,0.389,0.088,0.363,0.0456,0
            05xDjWH9ub67nJJk82yfGf,['Ignacio Corsini'],La Mañanita - Remasterizado,1981,0,161520,64.678,-21.508,0.721,0.996,0.485,0.13,0.104,0.0483,0
            0MJZ4hh60zwsYleWWxT5yW,['Zay Gatsby'],Power Is Power,1981,0,205072,159.935,-7.298,0.493,0.0175,0.527,0.691,0.358,0.0326,1""";

    @Before
    public void setup() throws IOException {
        try (Reader reader = new StringReader(text)) {
            e = new SpotifyExplorer(reader);
        }
        try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
            allTracks = reader.lines().skip(1).map(SpotifyTrack::of).collect(Collectors.toList());
        }
    }

    @Test
    public void getAllSpotifyTracksTest() {
        Collection<SpotifyTrack> tracks = e.getAllSpotifyTracks();
        assertEquals(allTracks.size(), tracks.size());
        assertTrue(tracks.containsAll(allTracks));
    }

    @Test
    public void getExplicitSpotifyTracksTest() {
        Collection<SpotifyTrack> tracks = e.getExplicitSpotifyTracks();
        int expectedSize = 1;
        assertEquals(expectedSize, tracks.size());
        assertTrue(tracks.contains(allTracks.get(9)));
    }

    @Test
    public void groupSpotifyTracksByYearTest() {
        Map<Integer, Set<SpotifyTrack>> tracksGroupedByYear = e.groupSpotifyTracksByYear();
        final int EXPECTED_KEYS = 4;
        final int EXPECTED_VALUES_1921 = 4;
        final int EXPECTED_VALUES_1924 = 2;
        final int EXPECTED_VALUES_1981 = 2;
        final int EXPECTED_VALUES_1999 = 2;
        assertEquals(EXPECTED_KEYS, tracksGroupedByYear.keySet().size());
        assertEquals(EXPECTED_VALUES_1921, tracksGroupedByYear.get(1921).size());
        assertEquals(EXPECTED_VALUES_1924, tracksGroupedByYear.get(1924).size());
        assertEquals(EXPECTED_VALUES_1981, tracksGroupedByYear.get(1981).size());
        assertEquals(EXPECTED_VALUES_1999, tracksGroupedByYear.get(1999).size());
    }

    @Test
    public void getArtistActiveYearsTest() {
        final String ARTIST = "KHP Kridhamardawa Karaton Ngayogyakarta Hadiningrat";
        int activeYears = e.getArtistActiveYears(ARTIST);
        int expectedActiveYears = 4;
        assertEquals(expectedActiveYears, activeYears);
    }

    @Test
    public void getTopNHighestValenceTracksFromThe80sTest() {
        List<SpotifyTrack> tracks = e.getTopNHighestValenceTracksFromThe80s(1);
        int expectedSize = 1;
        assertEquals(expectedSize, tracks.size());
        assertEquals(0, tracks.get(0).id().compareTo(allTracks.get(8).id()));
    }

    @Test
    public void getMostPopularTrackFromThe90sTest() {
        SpotifyTrack track = e.getMostPopularTrackFromThe90s();
        assertEquals(allTracks.get(6), track);
    }

    @Test
    public void getNumberOfLongerTracksBeforeYearTest() {
        int minutes = 13;
        int year = 1922;
        long numberOfTracks = e.getNumberOfLongerTracksBeforeYear(minutes, year);
        int expectedNumberOfTracks = 1;
        assertEquals(expectedNumberOfTracks, numberOfTracks);
    }

    @Test
    public void getTheLoudestTrackInYearTest() {
        Optional<SpotifyTrack> track = e.getTheLoudestTrackInYear(1921);
        assertTrue(track.isPresent());
        assertEquals(allTracks.get(4), track.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNumberOfLongerTracksBeforeYearThrowsException() {
        long n = e.getNumberOfLongerTracksBeforeYear(-3, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTheLoudestTrackInYearTestThrowsIllegalArgumentException() {
        Optional<SpotifyTrack> track = e.getTheLoudestTrackInYear(-432);
    }
}
