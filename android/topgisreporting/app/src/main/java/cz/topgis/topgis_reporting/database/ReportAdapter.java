package cz.topgis.topgis_reporting.database;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cz.topgis.topgis_reporting.R;

/**
 * Adapter for main activity to listing reports
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.RowViewHolder>
{
	/**
	 * Report list
	 */
	private List<Report> reportsList;

	class RowViewHolder extends RecyclerView.ViewHolder
	{
		TextView textViewDate;
		TextView textViewTypeName;
		TextView textViewSend;

		RowViewHolder(View view)
		{
			super(view);
			textViewDate = (TextView) view.findViewById(R.id.text_view_date);
			textViewTypeName = (TextView) view.findViewById(R.id.text_view_type_name);
			textViewSend = (TextView) view.findViewById(R.id.text_view_send);
		}
	}


	public ReportAdapter(List<Report> reportsList)
	{
		this.reportsList = reportsList;
	}

	@NonNull
	@Override
	public RowViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
	{
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list_row, parent, false);

		return new RowViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(RowViewHolder holder, int position)
	{
		Report report = reportsList.get(position);
		holder.textViewDate.setText(report.getCreateTime().toString());
		holder.textViewTypeName.setText(report.getReportType().getReportType());
		holder.textViewSend.setText(report.isSend());
	}

	@Override
	public int getItemCount()
	{
		return reportsList.size();
	}
}