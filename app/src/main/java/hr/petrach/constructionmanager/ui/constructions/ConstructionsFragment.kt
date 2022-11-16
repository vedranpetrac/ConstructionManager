package hr.petrach.constructionmanager.ui.constructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hr.petrach.constructionmanager.App
import hr.petrach.constructionmanager.R
import hr.petrach.constructionmanager.databinding.FragmentConstructionsBinding
import hr.petrach.constructionmanager.ui.NavigableFragment
import hr.petrach.constructionmanager.ui.workers.WorkerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConstructionsFragment : Fragment(), NavigableFragment {

    private var _binding: FragmentConstructionsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConstructionsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadConstructions()
        setupListeners()
    }


    private fun loadConstructions() {
        GlobalScope.launch(Dispatchers.Main) { // dispatch in MAIN thread
            val constructionsAndContractor = withContext(Dispatchers.IO) { // work in IO thread
                (context?.applicationContext as App).getConstructionDao().getAllConstructionAndContractor()
            }
            binding.rvConstructions.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = ConstructionAdapter(requireContext(), constructionsAndContractor, this@ConstructionsFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.fbEditConstruction.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_constructions_to_navigation_constructions_edit)
        }
    }

    override fun navigate(bundle: Bundle) {
        findNavController().navigate(
            R.id.action_navigation_constructions_to_navigation_constructions_edit,
            bundle
        )

    }
}