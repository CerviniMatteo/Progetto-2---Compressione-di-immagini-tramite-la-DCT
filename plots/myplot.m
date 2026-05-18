clear; clc; close all;

%% =========================================================
% LOAD DATASETS
%% =========================================================
datasets(1).name  = "no_warmup";
datasets(1).table = readtable( ...
    "times_vs_size.csv", ...
    'VariableNamingRule', 'preserve');

datasets(2).name  = "with_warmup";
datasets(2).table = readtable( ...
    "times_vs_size_with_JIT_warm_up.csv", ...
    'VariableNamingRule', 'preserve');

%% =========================================================
% OUTPUT DIRECTORY
%% =========================================================
cmp_dir = "results";

if ~exist(cmp_dir, 'dir')
    mkdir(cmp_dir);
end

%% =========================================================
% LOOP OVER DATASETS
%% =========================================================
for k = 1:length(datasets)

    CSV          = datasets(k).table;
    dataset_name = datasets(k).name;

    %% =====================================================
    % NORMALIZE COLUMN NAMES
    %% =====================================================
    CSV.Properties.VariableNames = ...
        matlab.lang.makeValidName(CSV.Properties.VariableNames);

    % Display generated names (debug helper)
    disp("Generated variable names:");
    disp(CSV.Properties.VariableNames');
    %% =====================================================
    % RENAME VARIABLES TO SIMPLE NAMES
    %% =====================================================
    rename = {
        'Size',                    'n';
    
        'CustomAvg_s_',           'my_avg';
        'CustomMin_s_',           'my_min';
        'CustomMax_s_',           'my_max';
        'CustomSum_s_',           'my_sum';
        'CustomN',                'my_n';
    
        'LibraryAvg_s_',          'lib_avg';
        'LibraryMin_s_',          'lib_min';
        'LibraryMax_s_',          'lib_max';
        'LibrarySum_s_',          'lib_sum';
        'LibraryN',               'lib_n';
    
        'CustomSlowerThanLib___', 'pct_slower';
    };
    for r = 1:size(rename, 1)

        old_name = rename{r,1};
        new_name = rename{r,2};

        idx = strcmp(CSV.Properties.VariableNames, old_name);

        if any(idx)
            CSV.Properties.VariableNames{idx} = new_name;
        end
    end

    %% =====================================================
    % CONVENIENCE VARIABLES
    %% =====================================================
    n       = CSV.n;

    my_avg  = CSV.my_avg;
    my_min  = CSV.my_min;
    my_max  = CSV.my_max;

    lib_avg = CSV.lib_avg;
    lib_min = CSV.lib_min;
    lib_max = CSV.lib_max;

    %% =====================================================
    % ERROR BAR WIDTHS
    %% =====================================================
    my_lo  = my_avg  - my_min;
    my_hi  = my_max  - my_avg;

    lib_lo = lib_avg - lib_min;
    lib_hi = lib_max - lib_avg;

    %% =====================================================
    % SPEEDUP FACTOR
    %% =====================================================
    speedup = my_avg ./ lib_avg;

    %% =====================================================
    % THEORETICAL COMPLEXITIES
    %% =====================================================
    theory_n     = n;
    theory_n2    = n.^2;
    theory_n2log = n.^2 .* log2(n);
    theory_n3    = n.^3;

    %% =====================================================
    % COMMON GRAPH VARIABLES
    %% =====================================================
    x        = (1:numel(n))';
    n_labels = string(n);

    bw = 0.35;

    %% =====================================================
    % 1) SEMILOG PLOT
    %% =====================================================
    f1 = figure;

    semilogy(n, my_avg, '-o', 'LineWidth', 1.5);
    hold on;

    semilogy(n, lib_avg, '-s', 'LineWidth', 1.5);

    xlabel('Matrix size n');
    ylabel('Execution time (s, log-scale)');

    title(['Experimental execution time - ', dataset_name]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'Location', 'northwest');

    grid on;

    %% =====================================================
    % 2) LINEAR PLOT
    %% =====================================================
    f2 = figure;

    plot(n, my_avg, '-o', 'LineWidth', 1.5);
    hold on;

    plot(n, lib_avg, '-s', 'LineWidth', 1.5);

    xlabel('Matrix size n');
    ylabel('Execution time (s)');

    title(['Linear execution time - ', dataset_name]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'Location', 'northwest');

    grid on;

    ylim([0 max(my_avg) * 1.1]);

    %% =====================================================
    % 3) SEMILOG + THEORY
    %% =====================================================
    f3 = figure;

    semilogy(n, my_avg, '-o', 'LineWidth', 1.5);
    hold on;

    semilogy(n, lib_avg, '-s', 'LineWidth', 1.5);

    semilogy(n, theory_n, '--', 'LineWidth', 1.2);
    semilogy(n, theory_n2, '--', 'LineWidth', 1.2);
    semilogy(n, theory_n2log, '--', 'LineWidth', 1.2);
    semilogy(n, theory_n3, '--', 'LineWidth', 1.2);

    xlabel('Matrix size n');
    ylabel('Execution time (s, log-scale)');

    title(['Experimental vs theoretical - ', dataset_name]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'O(n)', ...
        'O(n^2)', ...
        'O(n^2 log n)', ...
        'O(n^3)', ...
        'Location', 'northwest');

    grid on;

    %% =====================================================
    % 4) LINEAR + THEORY NORMALIZED
    %% =====================================================
    f4 = figure;

    scale = max(my_avg) / max(theory_n2log);

    plot(n, my_avg, '-o', 'LineWidth', 1.5);
    hold on;

    plot(n, lib_avg, '-s', 'LineWidth', 1.5);

    plot(n, theory_n     * scale, '--', 'LineWidth', 1.2);
    plot(n, theory_n2    * scale, '--', 'LineWidth', 1.2);
    plot(n, theory_n2log * scale, '--', 'LineWidth', 1.2);
    plot(n, theory_n3    * scale, '--', 'LineWidth', 1.2);

    xlabel('Matrix size n');
    ylabel('Execution time (s)');

    title(['Linear normalized comparison - ', dataset_name]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'O(n)', ...
        'O(n^2)', ...
        'O(n^2 log n)', ...
        'O(n^3)', ...
        'Location', 'northwest');

    grid on;

    ylim([0 max(my_avg) * 1.2]);

    %% =====================================================
    % 5) HISTOGRAM — AVG TIME PER SIZE
    %% =====================================================
    f5 = figure;

    bar(x - bw/2, my_avg, bw, ...
        'FaceColor', [0.2 0.5 0.9]);

    hold on;

    bar(x + bw/2, lib_avg, bw, ...
        'FaceColor', [0.9 0.4 0.2]);

    hold off;

    set(gca, ...
        'XTick', x, ...
        'XTickLabel', n_labels);

    xlabel('Matrix size n');
    ylabel('Avg execution time (s)');

    title(['Avg time distribution across sizes - ', dataset_name]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'Location', 'northwest');

    grid on;

    %% =====================================================
    % 6) HISTOGRAM + ERROR BARS (LOG SCALE)
    %% =====================================================
    f6 = figure;

    bar(x - bw/2, my_avg, bw, ...
        'FaceColor', [0.2 0.5 0.9]);

    hold on;

    bar(x + bw/2, lib_avg, bw, ...
        'FaceColor', [0.9 0.4 0.2]);

    errorbar( ...
        x - bw/2, ...
        my_avg, ...
        my_lo, ...
        my_hi, ...
        '.k', ...
        'LineWidth', 1.2);

    errorbar( ...
        x + bw/2, ...
        lib_avg, ...
        lib_lo, ...
        lib_hi, ...
        '.k', ...
        'LineWidth', 1.2);

    hold off;

    set(gca, ...
        'YScale', 'log', ...
        'XTick', x, ...
        'XTickLabel', n_labels);

    xlabel('Matrix size n');
    ylabel('Execution time (s, log-scale)');

    title(['Avg time with min/max spread (log) - ', dataset_name]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'Min/Max range', ...
        'Location', 'northwest');

    grid on;

    %% =====================================================
    % 7) MY IMPLEMENTATION MIN / AVG / MAX
    %% =====================================================
    f7 = figure;

    bar(x, [my_min, my_avg, my_max], 'grouped');

    set(gca, ...
        'XTick', x, ...
        'XTickLabel', n_labels);

    xlabel('Matrix size n');
    ylabel('Execution time (s)');

    title(['My DCT-II: min / avg / max per size - ', dataset_name]);

    legend('Min', 'Avg', 'Max', 'Location', 'northwest');

    grid on;

    %% =====================================================
    % 8) LIBRARY MIN / AVG / MAX
    %% =====================================================
    f8 = figure;

    bar(x, [lib_min, lib_avg, lib_max], 'grouped');

    set(gca, ...
        'XTick', x, ...
        'XTickLabel', n_labels);

    xlabel('Matrix size n');
    ylabel('Execution time (s)');

    title(['JTransform DCT-II: min / avg / max per size - ', dataset_name]);

    legend('Min', 'Avg', 'Max', 'Location', 'northwest');

    grid on;

    %% =====================================================
    % 9) SPEEDUP BAR
    %% =====================================================
    f9 = figure;

    bar(x, speedup, 0.55, ...
        'FaceColor', [0.3 0.75 0.45]);

    hold on;

    yline(1, '--r', ...
        'LineWidth', 1.5, ...
        'Label', 'Equal speed');

    hold off;

    set(gca, ...
        'XTick', x, ...
        'XTickLabel', n_labels);

    xlabel('Matrix size n');
    ylabel('Speedup factor (My / Lib)');

    title(['Library speedup over custom DCT - ', dataset_name]);

    grid on;

    for i = 1:numel(n)

        text( ...
            x(i), ...
            speedup(i) + max(speedup) * 0.02, ...
            sprintf('%.0fx', speedup(i)), ...
            'HorizontalAlignment', 'center', ...
            'FontSize', 8);

    end

    %% =====================================================
    % 10) LOG-LOG COMPLEXITY ANALYSIS
    %% =====================================================
    log_n   = log2(n);

    log_my  = log2(my_avg);
    log_lib = log2(lib_avg);

    p_my  = polyfit(log_n, log_my, 1);
    p_lib = polyfit(log_n, log_lib, 1);

    f10 = figure;

    plot(log_n, log_my, ...
        'o-', ...
        'LineWidth', 1.5, ...
        'DisplayName', 'My DCT-II (data)');

    hold on;

    plot(log_n, log_lib, ...
        's-', ...
        'LineWidth', 1.5, ...
        'DisplayName', 'JTransform (data)');

    plot(log_n, polyval(p_my, log_n), ...
        '--', ...
        'LineWidth', 1.2, ...
        'DisplayName', ...
        sprintf('My fit: slope = %.2f', p_my(1)));

    plot(log_n, polyval(p_lib, log_n), ...
        ':', ...
        'LineWidth', 1.2, ...
        'DisplayName', ...
        sprintf('Lib fit: slope = %.2f', p_lib(1)));

    hold off;

    xlabel('log_2(n)');
    ylabel('log_2(avg time)');

    title(['Log-log complexity analysis - ', dataset_name]);

    legend('Location', 'northwest');

    grid on;

    annotation( ...
        'textbox', ...
        [0.13 0.65 0.38 0.18], ...
        'String', sprintf( ...
        'My slope  ≈ %.2f  -> O(n^{%.2f})\nLib slope ≈ %.2f  -> O(n^{%.2f})', ...
        p_my(1), p_my(1), ...
        p_lib(1), p_lib(1)), ...
        'BackgroundColor', 'w', ...
        'EdgeColor', 'k', ...
        'FitBoxToText', 'on', ...
        'FontSize', 9);

    %% =====================================================
    % 11) MARGINAL SLOWNESS
    %% =====================================================
    delta_n = diff(n);

    n_mid = (n(1:end-1) + n(2:end)) / 2;

    dmy_dn  = diff(my_avg)  ./ delta_n;
    dlib_dn = diff(lib_avg) ./ delta_n;

    f11 = figure;

    semilogy(n_mid, dmy_dn, '-o', 'LineWidth', 1.5);
    hold on;

    semilogy(n_mid, dlib_dn, '-s', 'LineWidth', 1.5);

    xlabel('Matrix size n (midpoint)');

    ylabel('\DeltaAvgTime / \DeltaN');

    title(['Marginal slowness growth - ', dataset_name]);

    legend( ...
        'My DCT-II', ...
        'JTransform DCT-II', ...
        'Location', 'northwest');

    grid on;

    %% =====================================================
    % SAVE FIGURES
    %% =====================================================
    saveas(f1,  fullfile(cmp_dir, ...
        strcat("figure_01_semilogy_", dataset_name, ".png")));

    saveas(f2,  fullfile(cmp_dir, ...
        strcat("figure_02_linear_", dataset_name, ".png")));

    saveas(f3,  fullfile(cmp_dir, ...
        strcat("figure_03_theory_semilogy_", dataset_name, ".png")));

    saveas(f4,  fullfile(cmp_dir, ...
        strcat("figure_04_theory_linear_", dataset_name, ".png")));

    saveas(f5,  fullfile(cmp_dir, ...
        strcat("figure_05_hist_avg_linear_", dataset_name, ".png")));

    saveas(f6,  fullfile(cmp_dir, ...
        strcat("figure_06_hist_avg_minmax_", dataset_name, ".png")));

    saveas(f7,  fullfile(cmp_dir, ...
        strcat("figure_07_hist_my_minmaxavg_", dataset_name, ".png")));

    saveas(f8,  fullfile(cmp_dir, ...
        strcat("figure_08_hist_lib_minmaxavg_", dataset_name, ".png")));

    saveas(f9,  fullfile(cmp_dir, ...
        strcat("figure_09_speedup_bar_", dataset_name, ".png")));

    saveas(f10, fullfile(cmp_dir, ...
        strcat("figure_10_loglog_complexity_", dataset_name, ".png")));

    saveas(f11, fullfile(cmp_dir, ...
        strcat("figure_11_marginal_slowness_", dataset_name, ".png")));

end

disp("All plots generated successfully.");