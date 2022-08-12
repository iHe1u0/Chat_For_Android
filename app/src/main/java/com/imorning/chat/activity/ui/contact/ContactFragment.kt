package com.imorning.chat.activity.ui.contact

import android.os.Bundle
import android.view.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imorning.chat.App
import com.imorning.chat.R
import com.imorning.chat.activity.ui.profile.ProfileViewModel
import com.imorning.chat.adapter.ContactRecyclerAdapter
import com.imorning.chat.databinding.FragmentContactBinding

class ContactFragment : Fragment() {

    companion object {
        private const val TAG = "ContactFragment"
    }

    private var _binding: FragmentContactBinding? = null

    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as App).userDatabase.userInfoDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentContactBinding.inflate(inflater, container, false)
        val root: View = binding.root

        viewModel.allContacts.observe(viewLifecycleOwner) { list ->
            binding.rvContactList.adapter = ContactRecyclerAdapter(list)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = binding.rvContactList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Add divider for recyclerView
        val divider = DividerItemDecoration(App.getContext(), DividerItemDecoration.VERTICAL)
        divider.setDrawable(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.recycler_view_divider,
                null
            )!!
        )
        // recyclerView.addItemDecoration(divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}