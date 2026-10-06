package com.riverlines.game.core.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.riverlines.game.R
import com.riverlines.game.databinding.ItemEcoChallengeBinding
import com.riverlines.game.domain.model.EcoChallenge

class EcoChallengeAdapter(
    private val items: List<EcoChallenge>
) : RecyclerView.Adapter<EcoChallengeAdapter.ChallengeHolder>() {

    class ChallengeHolder(val binding: ItemEcoChallengeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemEcoChallengeBinding.inflate(inflater, parent, false)
        return ChallengeHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChallengeHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context
        val accent = if (item.completed) {
            ContextCompat.getColor(context, R.color.color_primary)
        } else {
            ContextCompat.getColor(context, R.color.color_text_tertiary)
        }
        holder.binding.challengeTitle.text = item.title
        holder.binding.challengeDescription.text = item.description
        holder.binding.challengeReward.text = context.getString(R.string.eco_reward, item.reward)
        holder.binding.challengeStatusIcon.setImageResource(
            if (item.completed) R.drawable.ic_check else R.drawable.ic_lock
        )
        holder.binding.challengeStatusIcon.contentDescription = context.getString(
            if (item.completed) R.string.state_challenge_done else R.string.state_challenge_locked
        )
        ImageViewCompat.setImageTintList(
            holder.binding.challengeStatusIcon,
            ColorStateList.valueOf(accent)
        )
        holder.binding.challengeTitle.setTextColor(accent)
        holder.binding.root.alpha = if (item.completed) 1f else 0.6f
    }
}
