package csfyp.cs_fyp_android.event;

import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import csfyp.cs_fyp_android.R;
import csfyp.cs_fyp_android.databinding.PassedEventAttendanceItemBinding;
import csfyp.cs_fyp_android.lib.HTTP;
import csfyp.cs_fyp_android.lib.NoticeDialogFragment;
import csfyp.cs_fyp_android.lib.eventBus.RefreshFrg;
import csfyp.cs_fyp_android.lib.eventBus.ShowDialog;
import csfyp.cs_fyp_android.model.User;
import csfyp.cs_fyp_android.model.request.ChangeAttendanceRequest;
import csfyp.cs_fyp_android.model.respond.ErrorMsgOnly;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdtAttendanceUser extends RecyclerView.Adapter<AdtAttendanceUser.ViewHolder> {

    private List<User> mUserList;
    private int eventId;

    private NoticeDialogFragment mDialog;


    public AdtAttendanceUser(int eventId) {
        this.eventId = eventId;
    }

    @Override
    public int getItemCount() {
        if (mUserList != null)
            return mUserList.size();
        else
            return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        PassedEventAttendanceItemBinding binding;
        ViewHolder holder;

        binding = DataBindingUtil.inflate(inflater, R.layout.passed_event_attendance_item, parent, false);
        holder = new ViewHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.getBinding().setHandlers(holder);
        if (mUserList != null) {
            holder.getBinding().setItem(mUserList.get(position));
            if (mUserList.get(position).isAttended())
                holder.getBinding().notAttendBtn.setVisibility(View.GONE);
            else
                holder.getBinding().attendBtn.setVisibility(View.GONE);
            holder.getBinding().executePendingBindings();
        }
    }

    public void setmUserList(List<User> mUserList) {
        this.mUserList = mUserList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PassedEventAttendanceItemBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        // each data item is just a string in this case
        public void onClickAttendItem(View view) {

            binding.attendBtn.setVisibility(View.GONE);
            binding.attendanceProgressBar.setVisibility(View.VISIBLE);

            HTTP httpService = HTTP.retrofit.create(HTTP.class);
            Call<ErrorMsgOnly> call = httpService.changeAttendance(new ChangeAttendanceRequest(binding.getItem().getId(), eventId, false));

            call.enqueue(new Callback<ErrorMsgOnly>() {
                @Override
                public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                    if(response.isSuccessful() && response.body().getErrorMsg() == null) {
                        binding.notAttendBtn.setVisibility(View.VISIBLE);
                        binding.attendanceProgressBar.setVisibility(View.GONE);
                        EventBus.getDefault().post(1);
                    }
                }

                @Override
                public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                    binding.attendBtn.setVisibility(View.VISIBLE);
                    binding.attendanceProgressBar.setVisibility(View.GONE);
                }
            });
        }

        public void onClickNotAttendItem(View view) {

            mDialog = NoticeDialogFragment.newInstance(R.string.set_not_attended_dialog_title, R.string.set_not_attended_dialog_message);
            mDialog.setDialogListener(new NoticeDialogFragment.NoticeDialogListener() {
                @Override
                public void onDialogPositiveClick(DialogFragment dialog) {
                    binding.notAttendBtn.setVisibility(View.GONE);
                    binding.attendanceProgressBar.setVisibility(View.VISIBLE);

                    HTTP httpService = HTTP.retrofit.create(HTTP.class);
                    Call<ErrorMsgOnly> call = httpService.changeAttendance(new ChangeAttendanceRequest(binding.getItem().getId(), eventId, true));
                    call.enqueue(new Callback<ErrorMsgOnly>() {
                        @Override
                        public void onResponse(Call<ErrorMsgOnly> call, Response<ErrorMsgOnly> response) {
                            if(response.isSuccessful() && response.body().getErrorMsg() == null) {
                                binding.attendBtn.setVisibility(View.VISIBLE);
                                binding.attendanceProgressBar.setVisibility(View.GONE);

                                EventBus.getDefault().post(new RefreshFrg(FrgPassedEvent.TAG));
                            }
                        }

                        @Override
                        public void onFailure(Call<ErrorMsgOnly> call, Throwable t) {
                            binding.notAttendBtn.setVisibility(View.VISIBLE);
                            binding.attendanceProgressBar.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onDialogNegativeClick(DialogFragment dialog) {
                    mDialog.dismiss();
                }
            });

            EventBus.getDefault().post(new ShowDialog(mDialog));

        }

        public PassedEventAttendanceItemBinding getBinding() {
            return binding;
        }

        public void setBinding(PassedEventAttendanceItemBinding binding) {
            this.binding = binding;
        }
    }

}
