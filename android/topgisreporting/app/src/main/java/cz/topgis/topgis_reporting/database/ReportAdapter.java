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

	/**
	 * Inner class representing ViewHolder
	 */
	class RowViewHolder extends RecyclerView.ViewHolder
	{
		/**
		 * Represents date of creation
		 */
		TextView textViewDate;

		/**
		 * Represents report type
		 */
		TextView textViewTypeName;

		/**
		 * Represents if report has been send
		 */
		TextView textViewSend;

		/**
		 * Basic constructor
		 * @param view Represents textViews location
		 */
		RowViewHolder(View view)
		{
			super(view);
			// Connection views
			textViewDate = (TextView) view.findViewById(R.id.text_view_date);
			textViewTypeName = (TextView) view.findViewById(R.id.text_view_type_name);
			textViewSend = (TextView) view.findViewById(R.id.text_view_send);
		}
	}

	/**
	 * Basic constructor
	 * @param reportsList List of reports to show
	 */
	public ReportAdapter(List<Report> reportsList)
	{
		this.reportsList = reportsList;
	}


	@NonNull
	@Override
	public RowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
	{
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_list_row, parent, false);

		return new RowViewHolder(itemView);
	}

	/**
	 * Set content from Report to ViewHolder
	 * @param holder Holds views, which will be shown
	 * @param position Report position in the list
	 */
	@Override
	public void onBindViewHolder(@NonNull RowViewHolder holder, int position)
	{
		Report report = reportsList.get(position);

		holder.textViewDate.setText(report.getCreateTime());
		holder.textViewTypeName.setText(report.getReportType());
		this.setViewSend(report, holder);

	}

	/**
	 * Setter which takes string from resources
	 * @param report Report which will be managing
	 * @param holder Row holder which is used to RecyclerView
	 */
	private void setViewSend(Report report, RowViewHolder holder)
	{
		if(report.isSend()) holder.textViewSend.setText(R.string.report_sent_true);
		else holder.textViewSend.setText("");
	}

	/**
	 * Simple size getter
	 * @return Size of the list
	 */
	@Override
	public int getItemCount()
	{
		return reportsList.size();
	}
}