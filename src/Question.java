import org.json.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Question {
    int id; // unique ID for avery question
    String folder; // images folder
    String core; // Question declarative text ex: What sign does it represent ?
    private int points; // question reward
    ArrayList<String> propositions = new ArrayList<>(); // since all questions are QCMs ~ contains all propositions
    ArrayList<Boolean> answers = new ArrayList<>(); // since all questions are QCMs ~ contains all propositions

    String note; // foot note

    public Question loadFromJson(String filename, int id) throws IOException, ParseException {
        Scanner scanner = new Scanner( new File(filename), "UTF-8" );
        String jsonCourse = scanner.useDelimiter("\\A").next();
        scanner.close(); // Put this call in a finally block

        JSONObject obj = new JSONObject(jsonCourse);
        JSONArray Questions = obj.getJSONArray("Questions");
        JSONObject qstObj = Questions.getJSONObject(id - 1);
        this.id = id;
        this.points = qstObj.getInt("points");
        this.core = qstObj.getString("core");

        JSONArray props = qstObj.getJSONArray("propositions");
        JSONArray correct_answers = qstObj.getJSONArray("correct_answers");

        int nProps = props.length();
        if (nProps != correct_answers.length())
            throw new ParseException("Question " + core.substring(0, min(10, core.length())) + "... " + nProps
                    + "propositions || " + correct_answers.length() + " correct_answers provided", -1); // DEBUG MODE ONLY
        for (int i = 0; i < nProps; ++i) {
            String proposition = props.getString(i);
            boolean isCorrect = (correct_answers.getInt(i) != 0);
            this.propositions.add(proposition);
            this.answers.add(isCorrect);
        }

        this.note = qstObj.getString("note");
        this.folder = Settings.dataPath + "Questions-img\\" +  qstObj.getString("folder");

        return this;
    }

    public static int min(int a, int b) {
        return a <= b ? a : b;
    }

    public void show() // DEBUG_ONLY
    {
        Debug.debugMsg("" + this.core);
        Debug.debugMsg("" + "__________________________");
        for (int i = 0; i < propositions.size(); ++i) {
            Debug.debugMsg("" + i + ". " + propositions.get(i) + " | isCorrect = " + answers.get(i));
        }
        Debug.debugMsg("" + "__________________________");
        Debug.debugMsg("" + this.note);
    }

    public int getPoints() {
        return points;
    }
}
