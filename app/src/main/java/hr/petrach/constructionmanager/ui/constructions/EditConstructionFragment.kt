package hr.petrach.constructionmanager.ui.constructions


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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import hr.petrach.constructionmanager.App
import hr.petrach.constructionmanager.R
import hr.petrach.constructionmanager.dao.Construction
import hr.petrach.constructionmanager.dao.ConstructionAndContractor
import hr.petrach.constructionmanager.dao.Contractor
import hr.petrach.constructionmanager.databinding.FragmentConstructionEditBinding
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

const val CONSTRUCTION_ID = "constructionId"
private const val IMAGE_TYPE = "image/*"

class EditConstructionFragment : Fragment() {
    private var _binding: FragmentConstructionEditBinding? = null
    private lateinit var constructionAndContractor: ConstructionAndContractor
    var contractors: MutableList<Contractor>? = null
    var contractorSelected: Contractor? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentConstructionEditBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchContractors()
        fetchConstruction()
    }

    private fun fetchContractors() {


        GlobalScope.launch(Dispatchers.Default) {
            contractors = withContext(Dispatchers.IO) { // work in IO thread
                (context?.applicationContext as App).getWorkerDao().getWorkers()
            }
            val workersList: MutableList<Contractor> = mutableListOf()


            contractors?.forEach { w ->
                if (workersList != null) {
                    workersList.add(w)
                }
            }

            val spinner = binding.spinnerWorkers
            if (spinner != null) {
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, workersList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchConstruction() {
        val constructionId = arguments?.getLong(CONSTRUCTION_ID)
        if (constructionId != null) {
            GlobalScope.launch(Dispatchers.Main) { // dispatch in MAIN thread
                constructionAndContractor = withContext(Dispatchers.IO) { // work in IO thread
                    ((context?.applicationContext as App).getConstructionDao().getConstructionAndContractor(constructionId!!)
                        ?: ConstructionAndContractor(construction = Construction())) as ConstructionAndContractor
                }
                bindConstruction()
            }
        } else {
            constructionAndContractor = ConstructionAndContractor(construction = Construction())
            bindConstruction()
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK && it.data != null) {
                if (constructionAndContractor.construction.picturePath != null) {
                    File(constructionAndContractor.construction.picturePath).delete()
                }
                val dir = context?.applicationContext?.getExternalFilesDir(null)
                val file = File(dir, File.separator.toString() + UUID.randomUUID().toString() + ".jpg")
                context?.contentResolver?.openInputStream(it.data?.data as Uri).use { inputStream ->
                    FileOutputStream(file).use { fos ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val bos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        fos.write(bos.toByteArray())
                        constructionAndContractor.construction.picturePath = file.absolutePath
                        bindConstruction()
                    }
                }
            }

        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleImage() {
        Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = hr.petrach.constructionmanager.ui.constructions.IMAGE_TYPE
            imageResult.launch(this)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleStartDate() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                constructionAndContractor.construction.startDate = LocalDate.of(year, month, dayOfMonth)
                bindConstruction()
            },
            constructionAndContractor.construction.startDate.year,
            constructionAndContractor.construction.startDate.monthValue,
            constructionAndContractor.construction.startDate.dayOfMonth
        ).show()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleEndDate() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                constructionAndContractor.construction.endDate = LocalDate.of(year, month, dayOfMonth)
                bindConstruction()
            },
            constructionAndContractor.construction.endDate.year,
            constructionAndContractor.construction.endDate.monthValue,
            constructionAndContractor.construction.endDate.dayOfMonth
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bindConstruction() {
        if (constructionAndContractor.construction.picturePath != null) {
            Picasso.get()
                .load(File(constructionAndContractor.construction.picturePath))
                .transform(RoundedCornersTransformation(50, 5)) // radius, margin
                .into(binding.ivImage)
        } else {
            binding.ivImage.setImageResource(R.mipmap.ic_launcher)
        }
        binding.tvStartDate.text = constructionAndContractor.construction.startDate.format(DateTimeFormatter.ISO_DATE)
        binding.tvEndDate.text = constructionAndContractor.construction.endDate.format(DateTimeFormatter.ISO_DATE)
        binding.etFirstName.setText(constructionAndContractor.construction.projectName ?: "")
        binding.etAddress.setText(constructionAndContractor.construction.address ?: "")
        binding.etCity.setText(constructionAndContractor.construction.city ?: "")
        if(constructionAndContractor.contractor == null){
                binding.etContractor.setText("-")
        }else binding.etContractor.setText(constructionAndContractor.contractor.toString())


        setupListeners()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListeners() {
        binding.tvStartDate.setOnClickListener {
            handleStartDate()
        }
        binding.tvEndDate.setOnClickListener {
            handleEndDate()
        }
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
                constructionAndContractor.construction.projectName = text?.toString()?.trim()
            }
        })
        binding.etAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                constructionAndContractor.construction.address = text?.toString()?.trim()
            }
        })
        binding.etCity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                constructionAndContractor.construction.city = text?.toString()?.trim()
            }
        })

        binding.spinnerWorkers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    contractorSelected = parent.getItemAtPosition(position) as Contractor

                };
            }
        }
        binding.btnSetContractor.setOnClickListener{
            constructionAndContractor.construction.contractorSignedId = contractorSelected?.contractorId
            binding.etContractor.text = contractorSelected.toString()
        }
    }

    private fun formValid(): Boolean {
        var ok = true
        arrayOf(binding.etFirstName, binding.etCity, binding.etAddress).forEach {
            if (it.text.isNullOrEmpty()) {
                it.error = getString(R.string.please_insert_value)
                ok = false
            }
        }
        return ok && constructionAndContractor.construction.picturePath != null;
    }

    private fun commit() {

        GlobalScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                if (constructionAndContractor.construction?.constructionId == null) {
                    (context?.applicationContext as App).getConstructionDao().insert(constructionAndContractor.construction)
                } else {
                    (context?.applicationContext as App).getConstructionDao().update(constructionAndContractor.construction)
                }
            }
            findNavController().navigate(R.id.action_navigation_constructions_edit_to_navigation_constructions)
        }
    }

}