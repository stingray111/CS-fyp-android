package csfyp.cs_fyp_android.rating;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;

import java.util.ArrayList;
import java.util.List;

import csfyp.cs_fyp_android.CustomFragment;
import csfyp.cs_fyp_android.MainActivity;
import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.RatingFrgBinding;
import csfyp.cs_fyp_android.lib.CustomTag;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.model.request.Rate;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FrgRating extends CustomFragment {

    private static String TAG = "RatingFragment";

    private int mUserId;
    private int mEventId;
    private String mUsername;
    private RatingFrgBinding mDatabinding;
    private Toolbar mToolBar;
    private TagView addedTagGroup;

    private ArrayList<Tag> ETags = new ArrayList<>();
    private ArrayList<Tag> ATags = new ArrayList<>();
    private ArrayList<Tag> CTags = new ArrayList<>();
    private ArrayList<Tag> NTags = new ArrayList<>();
    private ArrayList<Tag> OTags = new ArrayList<>();


    public FrgRating() {}

    public static FrgRating newInstance(int userId, String username, int evenId) {

        Bundle args = new Bundle();
        args.putInt("userId", userId);
        args.putString("username", username);
        args.putInt("eventId", evenId);

        FrgRating fragment = new FrgRating();
        fragment.setArguments(args);
        return fragment;
    }

    private void addAdjectives() {

        // Extraversion
        ETags.add(new CustomTag(1, getString(R.string.outgoing), R.color.yellow).getTag());
        ETags.add(new CustomTag(2, getString(R.string.enthusiastic), R.color.yellow).getTag());
        ETags.add(new CustomTag(3, getString(R.string.active), R.color.yellow).getTag());

        ETags.add(new CustomTag(4, getString(R.string.aloof), R.color.yellow).getTag());
        ETags.add(new CustomTag(5, getString(R.string.quiet), R.color.yellow).getTag());
        ETags.add(new CustomTag(6, getString(R.string.independent), R.color.yellow).getTag());

        // Agreeableness
        ATags.add(new CustomTag(7, getString(R.string.trusting), R.color.purple).getTag());
        ATags.add(new CustomTag(8, getString(R.string.empathetic), R.color.purple).getTag());
        ATags.add(new CustomTag(9, getString(R.string.compliant), R.color.purple).getTag());

        ATags.add(new CustomTag(10, getString(R.string.uncooperative), R.color.purple).getTag());
        ATags.add(new CustomTag(11, getString(R.string.unempathetic), R.color.purple).getTag());
        ATags.add(new CustomTag(12, getString(R.string.hostile), R.color.purple).getTag());

        // Conscientiousness
        CTags.add(new CustomTag(13, getString(R.string.organized), R.color.blue).getTag());
        CTags.add(new CustomTag(14, getString(R.string.selfDirected), R.color.blue).getTag());
        CTags.add(new CustomTag(15, getString(R.string.dependable), R.color.blue).getTag());

        CTags.add(new CustomTag(16, getString(R.string.spontaneous), R.color.blue).getTag());
        CTags.add(new CustomTag(17, getString(R.string.careless), R.color.blue).getTag());
        CTags.add(new CustomTag(18, getString(R.string.proneToAddiction), R.color.blue).getTag());

        // Neuroticism
        NTags.add(new CustomTag(19, getString(R.string.irritable), R.color.red).getTag());
        NTags.add(new CustomTag(20, getString(R.string.moody), R.color.red).getTag());

        NTags.add(new CustomTag(21, getString(R.string.stable), R.color.red).getTag());
        NTags.add(new CustomTag(22, getString(R.string.relaxed), R.color.red).getTag());  // ??

        // Openness
        OTags.add(new CustomTag(23, getString(R.string.openToNewExperiences), R.color.green).getTag());
        OTags.add(new CustomTag(24, getString(R.string.curious), R.color.green).getTag());
        OTags.add(new CustomTag(25, getString(R.string.creative), R.color.green).getTag());
        OTags.add(new CustomTag(26, getString(R.string.imaginative), R.color.green).getTag());

        OTags.add(new CustomTag(27, getString(R.string.practical), R.color.green).getTag());
        OTags.add(new CustomTag(28, getString(R.string.conventional), R.color.green).getTag());
        OTags.add(new CustomTag(29, getString(R.string.skeptical), R.color.green).getTag());
        OTags.add(new CustomTag(30, getString(R.string.rational), R.color.green).getTag());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        Bundle args = getArguments();
        mUserId = args.getInt("userId");
        mUsername = args.getString("username");
        mEventId = args.getInt("eventId");

        mDatabinding = DataBindingUtil.inflate(inflater, R.layout.rating_frg, container, false);
        View v  = mDatabinding.getRoot();

        mToolBar = mDatabinding.ratingToolBar;
        AppCompatActivity parentActivity = (AppCompatActivity)getActivity();
        parentActivity.setSupportActionBar(mToolBar);
        mToolBar.setNavigationIcon(R.drawable.ic_previous_page);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack(TAG);
            }
        });

        mDatabinding.setUsername(mUsername + " is ...");
        mDatabinding.setHandlers(this);

        addedTagGroup = mDatabinding.addedTagGroup;
        final TagView ETagGroup = mDatabinding.ETagGroup;
        final TagView NTagGroup = mDatabinding.NTagGroup;
        final TagView CTagGroup = mDatabinding.CTagGroup;
        final TagView ATagGroup = mDatabinding.ATagGroup;
        final TagView OTagGroup = mDatabinding.OTagGroup;

        addAdjectives();

        ETagGroup.addTags(ETags);
        NTagGroup.addTags(NTags);
        CTagGroup.addTags(CTags);
        ATagGroup.addTags(ATags);
        OTagGroup.addTags(OTags);

        TagView.OnTagClickListener CustomTagClickListerner = new TagView.OnTagClickListener() {

            @Override
            public void onTagClick(Tag tag, int i) {
                if (addedTagGroup.getTags().size() < 5) {
                    addedTagGroup.addTag(tag);
                    if (tag.id >=1 && tag.id <=6)
                        ETagGroup.remove(i);
                    if (tag.id >=7 && tag.id <=12)
                        ATagGroup.remove(i);
                    if (tag.id >= 13 && tag.id <=18)
                        CTagGroup.remove(i);
                    if (tag.id >= 19 && tag.id <= 22)
                        NTagGroup.remove(i);
                    if (tag.id >= 23 && tag.id <= 30)
                        OTagGroup.remove(i);
                }
            }
        };

        ETagGroup.setOnTagClickListener(CustomTagClickListerner);
        NTagGroup.setOnTagClickListener(CustomTagClickListerner);
        CTagGroup.setOnTagClickListener(CustomTagClickListerner);
        ATagGroup.setOnTagClickListener(CustomTagClickListerner);
        OTagGroup.setOnTagClickListener(CustomTagClickListerner);


        //set click listener
        addedTagGroup.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int i) {
                addedTagGroup.remove(i);
                if (tag.id >=1 && tag.id <=6)
                    ETagGroup.addTag(tag);
                if (tag.id >=7 && tag.id <=12)
                    ATagGroup.addTag(tag);
                if (tag.id >= 13 && tag.id <=18)
                    CTagGroup.addTag(tag);
                if (tag.id >= 19 && tag.id <= 22)
                    NTagGroup.addTag(tag);
                if (tag.id >= 23 && tag.id <= 30)
                    OTagGroup.addTag(tag);
            }
        });

        return v;
    }

    public void onClickRateSubmit(View v) {

        mDatabinding.selfRateSubmitBtn.setVisibility(View.GONE);
        mDatabinding.selfRateSubmitBtnProgressBar.setVisibility(View.VISIBLE);

        int e = 0;
        int a = 0;
        int c = 0;
        int n = 0;
        int o = 0;

        List<Tag> temp = addedTagGroup.getTags();
        for (Tag tag: temp) {
            if (tag.id >=1 && tag.id <=3)
                e++;
            if (tag.id >=4 && tag.id <=6)
                e--;
            if (tag.id >=7 && tag.id <=9)
                a++;
            if (tag.id >=10 && tag.id <=12)
                a--;
            if (tag.id >= 13 && tag.id <=15)
                c++;
            if (tag.id >= 16 && tag.id <=18)
                c--;
            if (tag.id >= 19 && tag.id <= 20)
                n++;
            if (tag.id >= 21 && tag.id <= 22)
                n--;
            if (tag.id >= 23 && tag.id <= 26)
                o++;
            if (tag.id >= 27 && tag.id <= 30)
                o--;
        }



        HTTP httpService = HTTP.retrofit.create(HTTP.class);
        Call<ErrorMsgOnly> call = httpService.postRate(new Rate(e, a, c, n, o, ((MainActivity)getActivity()).getmUserId(), mUserId, mEventId));
        call.enqueue(new Callback<ErrorMsgOnly>() {
            @Override
            public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                onBack(null);
            }

            @Override
            public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {

            }
        });
    }
}
