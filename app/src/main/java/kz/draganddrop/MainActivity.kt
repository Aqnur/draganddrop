package kz.draganddrop

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kz.draganddrop.data.Card
import kz.draganddrop.data.CardTypeEnums
import kz.draganddrop.data.CombinedItem
import kz.draganddrop.databinding.ActivityMainBinding
import kz.draganddrop.utils.RecyclerViewItemClickCallback

class MainActivity : AppCompatActivity(), RecyclerViewItemClickCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: AdapterCard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        adapter = AdapterCard(this, binding.recyclerView)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        initList()
    }

    private fun initList() {
        val listCards = (0..10).map { i ->
            Card(
                id = i.toString(),
                type = CardTypeEnums.CARD,
                name = "Card $i",
                amount = "10 000 000"
            )
        }
        val listDeposits =  (10..20).map { i ->
            Card(
                id = i.toString(),
                type = CardTypeEnums.DEPOSIT,
                name = "Deposit $i",
                amount = "10 000 000"
            )
        }

        val items = mutableListOf<CombinedItem>()

        items += CombinedItem.Header("Cards")
        items += listCards.map { CombinedItem.CardItem(it) }

        items += CombinedItem.Header("Deposits")
        items += listDeposits.map { CombinedItem.DepositItem(it) }

        adapter.submitList(items)
    }

    override fun onRecyclerViewItemClick(any: Any) {

    }

    override fun onDragAndDrop(fromItem: CombinedItem, toItem: CombinedItem) {
        val fromName = when (fromItem) {
            is CombinedItem.CardItem -> "Card: ${fromItem.card.name}"
            is CombinedItem.DepositItem -> "Deposit: ${fromItem.deposit.name}"
            is CombinedItem.Header -> "Header"
        }

        val toName = when (toItem) {
            is CombinedItem.CardItem -> "Card: ${toItem.card.name}"
            is CombinedItem.DepositItem -> "Deposit: ${toItem.deposit.name}"
            is CombinedItem.Header -> "Header"
        }

        AlertDialog.Builder(this)
            .setTitle("Transfer Funds")
            .setMessage("Transfer from $fromName to $toName?")
            .setPositiveButton("Transfer") { _, _ ->
                // Implement transfer logic
                Toast.makeText(this, "Transferred!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}