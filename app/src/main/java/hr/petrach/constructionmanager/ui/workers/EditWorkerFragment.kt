package hr.petrach.constructionmanager.ui.workers

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import hr.petrach.constructionmanager.App
import hr.petrach.constructionmanager.R
import hr.petrach.constructionmanager.dao.Contractor
import hr.petrach.constructionmanager.databinding.FragmentWorkerEditBinding
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */

const val WORKER_ID = "workerId"
private const val IMAGE_TYPE = "image/*"

class EditWorkerFragment : Fragment() {
    private var _binding: FragmentWorkerEditBinding? = null
    private lateinit var contractor: Contractor

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWorkerEditBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchWorker()
        setupListeners()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWorker() {
        val workerId = arguments?.getLong(WORKER_ID)
        if (workerId != null) {
            GlobalScope.launch(Dispatchers.Main) { // dispatch in MAIN thread
                contractor = withContext(Dispatchers.IO) { // work in IO thread
                    ((context?.applicationContext as App).getWorkerDao().getWorker(workerId!!)
                        ?: Contractor()) as Contractor
                }
                bindWorker()
            }
        } else {
            contractor = Contractor()
            bindWorker()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindWorker() {
        if (contractor.picturePath != null) {
            Picasso.get()
                .load(File(contractor.picturePath))
                .transform(RoundedCornersTransformation(50, 5)) // radius, margin
                .into(binding.ivImage)
        } else {
            binding.ivImage.setImageResource(R.mipmap.ic_launcher)
        }
        binding.etFirstName.setText(contractor.name ?: "")
        binding.etLastName.setText(contractor.description ?: "")
        binding.etPosition.setText(contractor.specialization ?: "")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        binding.ivImage.setOnLongClickListener {
            handleImage()
            true
        }
        binding.btnCommit.setOnClickListener {
            if (formValid()) {
                commit()
            }
        }
        binding.etFirstName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                contractor.name = text?.toString()?.trim()
            }
        })
        binding.etLastName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                contractor.description = text?.toString()?.trim()
            }
        })
        binding.etPosition.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                contractor.specialization = text?.toString()?.trim()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                if (contractor.picturePath != null) {
                    File(contractor.picturePath).delete()
                }
                val dir = context?.applicationContext?.getExternalFilesDir(null)
                val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + ".jpg")
                context?.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                    FileOutputStream(file).use { fos ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        fos.write(bos.toByteArray())
                        contractor.picturePath = file.absolutePath
                        bindWorker()
                    }
                }
            }

        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = IMAGE_TYPE
            imageResult.launch(this)
        }
    }


    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.etFirstName, binding.etLastName, binding.etPosition).forEach {
            if (it.text.isNullOrEmpty()) {
                it.error = getString(R.string.please_insert_value)
                ok = false
            }
        }
        return ok && contractor.picturePath != null;
    }

    private fun commit() {
        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                if (contractor.contractorId == null) {
                    (context?.applicationContext as App).getWorkerDao().insert(contractor)
                } else {
                    (context?.applicationContext as App).getWorkerDao().update(contractor)
                }
            }
            findNavController().navigate(R.id.action_navigation_workers_edit_to_navigation_workers)
        }
    }

}