package app.sembrando.vidas.java_class;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import app.sembrando.vidas.classes.Ranking;
import com.example.yoplantounarbolito_app.R;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterDates extends RecyclerView.Adapter<AdapterDates.ViewHolderDates> {

    ArrayList<Ranking> list_item_ranking;

    public AdapterDates(ArrayList<Ranking> list_item_ranking) {
        this.list_item_ranking = list_item_ranking;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolderDates onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        return new ViewHolderDates(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolderDates holder, int position) {
        holder.indice.setText(list_item_ranking.get(position).getIndice()+"");
        holder.name_user.setText(list_item_ranking.get(position).getName_user());
        holder.name_tree.setText(list_item_ranking.get(position).getName_tree());
        holder.avatar.setImageResource(list_item_ranking.get(position).getAvatar());
        holder.points.setText(list_item_ranking.get(position).getPoints());
    }

    @Override
    public int getItemCount() {
        return list_item_ranking.size();
    }

    public class ViewHolderDates extends RecyclerView.ViewHolder {
        TextView indice, name_user, name_tree, points;
        ImageView avatar;

        public ViewHolderDates(@NonNull @NotNull View itemView) {
            super(itemView);
            indice = itemView.findViewById(R.id.id_item_indice);
            name_user = itemView.findViewById(R.id.id_item_name_user);
            name_tree = itemView.findViewById(R.id.id_item_name_tree);
            avatar= itemView.findViewById(R.id.avatar_ranking);
            points = itemView.findViewById(R.id.id_item_points);
        }
    }
}
