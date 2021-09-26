package com.example.videoads;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity{
    Button btn_rewardedAdsShow;
    TextView tv_rewardedAdsCoin;
    RewardedAd mRewardedAd;

    int previous_Coins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                loadRewardAds();
            }
        });

        //view finding
        btn_rewardedAdsShow=findViewById(R.id.btn_rewardedAdsShow);
        tv_rewardedAdsCoin=findViewById(R.id.tv_rewardedAdsCoin);



        btn_rewardedAdsShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    Activity activityContext = MainActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.

                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();

                            String preCoin=tv_rewardedAdsCoin.getText().toString();
                            if (preCoin.isEmpty()){
                                previous_Coins=0;
                            }else {
                                 previous_Coins=Integer.parseInt(preCoin);
                            }
                            int total_amount=previous_Coins+rewardAmount;

                            tv_rewardedAdsCoin.setText(String.valueOf(total_amount));
                           // tv_rewardedAdsCoin.setText("Amount:"+total_amount+"\nType"+rewardType);


                            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when ad is shown.
                                    Log.d("TAG", "Ad was shown.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when ad fails to show.
                                    Log.d("TAG", "Ad failed to show.");
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    Handler handler=new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadRewardAds();
                                        }
                                    },12000);

                                    mRewardedAd = null;
                                    super.onAdDismissedFullScreenContent();

                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    //Log.d("TAG", "Ad was dismissed.");

                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "ads is not available for this time", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadRewardAds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, getString(R.string.reward_ads_id),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("TAG", loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("TAG", "Ad was loaded.");
                    }
                });
    }

}