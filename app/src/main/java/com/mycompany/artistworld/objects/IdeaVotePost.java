package com.mycompany.artistworld.objects;

/**
 * Created by ygarcia on 10/5/2017.
 */

public class IdeaVotePost {

    private String idea;
    private int vote_weight;

    public IdeaVotePost(String idea, int vote_weight) {
        this.idea = idea;
        this.vote_weight = vote_weight;
    }
}
