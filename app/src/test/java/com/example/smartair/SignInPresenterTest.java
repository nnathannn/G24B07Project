package com.example.smartair;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.fragment.app.FragmentActivity;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class SignInPresenterTest {
    @Mock
    SignInFragment mockView;

    SignInPresenter mockPresenter;

    @Mock
    SignInModel mockModel;

    @Mock
    FragmentActivity mockActivity;

    @Before
    public void setUpFields() {
        mockView = mock(SignInFragment.class);
        mockModel = mock(SignInModel.class);
        mockActivity = mock(FragmentActivity.class);

        when(mockView.getActivity()).thenReturn(mockActivity);
        when(mockView.getContext()).thenReturn(mockActivity);

        mockPresenter = new SignInPresenter(mockView, mockModel);
    }

    // attemptSignIn with empty user field
    @Test
    public void attemptSignInTest1() {
        mockPresenter.attemptSignIn("", "password");
        verify(mockView, times(1)).displayErrorToast("Logic error: Please fill in all fields.");
    }

    // attemptSignIn with no @ in user field
    @Test
    public void attemptSignInTest2() {
        String username = "fakeuser";
        String password = "fakepass";

        mockPresenter.attemptSignIn(username, password);

        ArgumentCaptor<String> userCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockModel).signIn(eq(mockPresenter), userCaptor.capture(), passCaptor.capture());

        // The presenter should append the fake domain
        assert(userCaptor.getValue().equals("fakeuser@g24b07project.examplefakedomain"));
        assert(passCaptor.getValue().equals("fakepass"));
    }

    // attemptSignIn with @ in user field
    @Test
    public void attemptSignInTest3() {
        mockPresenter.attemptSignIn("fakeuser@gmail.com", "fakepass");
        verify(mockModel).signIn(mockPresenter, "fakeuser@gmail.com", "fakepass");
    }

    // startSignIn with parent role
    @Test
    public void startSignInTest1() {
        mockPresenter.startSignIn("parent");
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(mockView).startActivity(captor.capture());
        Intent capturedIntent = captor.getValue();
        assertEquals(HomeParent.class.getName(), capturedIntent.getComponent().getClassName());
    }

    // startSignIn with provider role
    @Test
    public void startSignInTest2() {
        mockPresenter.startSignIn("provider");
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(mockView).startActivity(captor.capture());
        Intent capturedIntent = captor.getValue();
        assertEquals(HomeProvider.class.getName(), capturedIntent.getComponent().getClassName());
    }

    // startSignIn with child role
    @Test
    public void startSignInTest3() {
        mockPresenter.startSignIn("parent");
        ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(mockView).startActivity(captor.capture());
        Intent capturedIntent = captor.getValue();
        assertEquals(ChildActivity.class.getName(), capturedIntent.getComponent().getClassName());
    }

    // showError with logic error
    @Test
    public void showErrorTest1() {
        mockPresenter.showError("Fake error message", "logic");
        verify(mockView).displayErrorToast("Logic error: Fake error message");
    }

    // showError with database error
    @Test
    public void showErrorTest2() {
        mockPresenter.showError("Fake error message", "database");
        verify(mockView).displayErrorToast("Database error: Fake error message");
    }
}

