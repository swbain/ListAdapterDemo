package aero.panasonic.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_stuff.*
import java.util.concurrent.TimeUnit


class StuffActivity : Activity() {

    val getStuff = GetStuff()

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stuff)

        val adapter = StuffAdapter()

        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter

        disposable = getStuff().subscribe { stuffList -> adapter.submitList(stuffList) }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}

class StuffAdapter : ListAdapter<Stuff, StuffViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StuffViewHolder {
        val view = TextView(parent.context)
        return StuffViewHolder(view)
    }

    override fun onBindViewHolder(holder: StuffViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: StuffViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stuff>() {
            override fun areItemsTheSame(oldItem: Stuff, newItem: Stuff): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stuff, newItem: Stuff): Boolean {
                return oldItem.id == newItem.id && oldItem.isFavorite == newItem.isFavorite
            }

            override fun getChangePayload(oldItem: Stuff, newItem: Stuff): Any? {
                return if (oldItem.isFavorite != newItem.isFavorite) FavoriteChangePayload
                else null
            }

        }
    }
}

class StuffViewHolder(private val view: TextView) : RecyclerView.ViewHolder(view) {
    fun bind(stuff: Stuff) {
        view.text = "${stuff.thing} ${stuff.isFavorite}"
    }
}

data class Stuff(val id: String, val thing: String, val isFavorite: Boolean)

class GetStuff {
    operator fun invoke(): Observable<List<Stuff>> {
        return Observable.just(
            listOf(
                Stuff(id = "1", thing = "thing 1", isFavorite = false),
                Stuff(id ="2", thing = "thing 2", isFavorite = true)
            )
        ).concatWith(
            Observable.just(
                listOf(
                    Stuff(id = "1", thing = "thing 1", isFavorite = true),
                    Stuff(id ="2", thing = "thing 2", isFavorite = true)
                )
            ).delay(3, TimeUnit.SECONDS)
        )
    }
}

object FavoriteChangePayload