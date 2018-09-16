package com.example.veasnahan.locatepublic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;
import com.example.veasnahan.locatepublic.R;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {


    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Product> productList;

    //getting the context and product list with constructor
    public ProductAdapter(Context mCtx, List<Product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from ( mCtx );
        View view = inflater.inflate ( R.layout.content_home_two, null );
        return new ProductViewHolder ( view );
    }


    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        //getting the product of the specified position
        final Product product = productList.get ( position );

        //binding the data with the viewholder views
        holder.textViewTitle.setText ( product.getHeader () );
        holder.textViewShortDesc.setText ( product.getTitle () );
        Picasso.get ().load ( product.getImageUrl () ).into ( holder.imageView );
        //if (product.getByBrand ()) {
            holder.relativeLayout.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mCtx,GridTwoLine.class);
                    intent.putExtra("itemname", product.getName ());
                    mCtx.startActivity(intent);
                    //Log.i("TAG", product.getHeader());
                    //Toast.makeText ( view.getContext (), product.getHeader (), Toast.LENGTH_SHORT ).show ();
                }
            } );
        //}
        holder.txtBrand.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Log.i ( "TAG", product.getName () );
//take all brand in item list

                Intent intent = new Intent(mCtx,GridTwoLine.class);
                intent.putExtra("itemname", product.getName ());
                mCtx.startActivity(intent);
            }
        } );
        if (product.getByBrand ()) {
            holder.txtBrand.setVisibility ( View.VISIBLE );
        } else {
            holder.txtBrand.setVisibility ( View.INVISIBLE );
        }
    }


    @Override
    public int getItemCount() {
        return productList.size ();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTitle, textViewShortDesc, textViewRating, textViewPrice;
        ImageView imageView;
        RelativeLayout relativeLayout;
        TextView txtBrand;

        public ProductViewHolder(View itemView) {
            super ( itemView );

            textViewTitle = itemView.findViewById (R.id.person_name);
            textViewShortDesc = itemView.findViewById ( R.id.person_age );
            imageView = itemView.findViewById ( R.id.person_photo );
            txtBrand = itemView.findViewById ( R.id.brandSearch );

            relativeLayout = itemView.findViewById ( R.id.layoutincard );
        }
    }
}