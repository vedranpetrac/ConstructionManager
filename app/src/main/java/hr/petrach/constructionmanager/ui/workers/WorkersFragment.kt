package hr.petrach.constructionmanager.ui.workers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hr.petrach.constructionmanager.App
import hr.petrach.constructionmanager.R
import hr.petrach.constructionmanager.databinding.FragmentWorkersBinding
import hr.petrach.constructionmanager.ui.NavigableFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkersFragment : Fragment(), NavigableFragment {

    private var _binding: FragmentWorkersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentWorkersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadWorkers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.fbEditWorker.setOnClickListener{
            findNavController().navigate(R.id.action_navigation_workers_to_navigation_workers_edit)
        }
    }


    private fun loadWorkers() {
        GlobalScope.launch(Dispatchers.Main) { // dispatch in MAIN thread
            val workers = withContext(Dispatchers.IO) { // work in IO thread
                (context?.applicationContext as App).getWorkerDao().getWorkers()
            }
            binding.rvWorkers.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = WorkerAdapter(requireContext(), workers, this@WorkersFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun navigate(bundle: Bundle) {
        findNavController().navigate(R.id.action_navigation_workers_to_navigation_workers_edit,bundle)
    }
}