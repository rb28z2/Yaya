package ca.currybox.daru;

/**
 * Created by Kanchana on 9/7/2014.
 */
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class info extends Activity
{

    public info()
    {
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent(); //get initial Intent from calling app

        String path = intent.getDataString(); //gets the full uri of the episode (usually a http url from a local proxy)

        TextView filename = (TextView)findViewById(R.id.data); //id of the textview used to show the cleaned up filename
        filename.setText(path); //sets the path (why is this even here? its getting replaced below...)

        String s4; //temporary strings to hold the niceified names
        String s5; //random

        try
        {
            s4 = URLDecoder.decode(path, "UTF-8"); //un-escapes the uri path because dicks and stupid %20s and shit
        }
        catch (UnsupportedEncodingException unsupportedencodingexception)
        {
            throw new AssertionError("UTF-8 is unknown"); //likes to throw this error for no reason. this ignores it
        }

        s5 = s4.substring(1 + s4.lastIndexOf("/")).replaceAll("\\[\\S+\\]", ""); //removes things in between square brackets (probably)

        if (s5.indexOf(" ") == 0)
        {
            s5 = s5.substring(1); //removes leading spaces
        }

        if (s5.charAt(s5.lastIndexOf(".")-1) == ' ')
        {
            s5 = (new StringBuilder()).append(s5.substring(0, -1 + s5.lastIndexOf("."))).append(s5.substring(s5.lastIndexOf("."))).toString(); //wat? why string builder? wtf is going on. stupid decompilation
            //seriously, rewrite this shit. this is terrible
        }

        filename.setText(s5); //replaces the text in the textview... stupid
    }
}
