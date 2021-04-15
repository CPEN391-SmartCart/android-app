package com.cpen391.smartcart

import com.cpen391.smartcart.ui.shopping.ShoppingViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import okhttp3.internal.tls.OkHostnameVerifier.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor

class ShoppingViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: ShoppingViewModel


    @Before
    fun before() {
        viewModel = ShoppingViewModel()
    }

    @Test
    fun testUserViewModel() {
        val expectedUser = viewModel.user.value
        viewModel.setName("John")

        val captor = ArgumentCaptor.forClass(User::class.java)
        captor.run {
            verify(observer, times(2)).onChanged(capture())
            assertNotNull(expectedUser)
            assertEquals("John", value.name)
        }
    }
}