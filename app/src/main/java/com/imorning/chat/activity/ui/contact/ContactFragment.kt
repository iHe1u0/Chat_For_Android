package com.imorning.chat.activity.ui.contact

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.imorning.chat.R
import com.imorning.chat.databinding.FragmentContactBinding


class ContactFragment : Fragment() {

    companion object {
        private const val TAG = "ContactFragment"
    }

    private var _binding: FragmentContactBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val contactViewModel =
            ViewModelProvider(this)[ContactViewModel::class.java]

        _binding = FragmentContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val rvContactList: RecyclerView = binding.rvContactList
        contactViewModel.text.observe(viewLifecycleOwner) { data ->
            // textView.text = data
        }

        binding.toolbarContact.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.main_contact_menu_add -> {
                        true
                    }
                    else -> false
                }
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}